import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

/**
 * ElevatorController is in charge of controlling its Elevator to fulfil the request of the passenger.
 * ElevatorController communicates by receiveing and sending  UDP messages to a well known port on it's Elevator.
 * 
 * @author Dominique Giguere Samson
 * @version March 12, 2022
 *
 */
public class ElevatorController implements Runnable {

	//Request Queue
	private ArrayList<int[]> requests; //[time, source, dir, dest]
	private int[] activeJob;
	private DatagramSocket sendReceiveSocket;
	private DatagramPacket receivePacket, sendPacket;
 	//Port of Elevator
	private int port;
	//Keep track of the elevator's motor
	private boolean moving; 
	//elevatorInfo = [current floor, direction, dest floor,# passengers]
	private int[] elevatorInfo;
	//inUse is true when the controller is executing a request;
	boolean inUse;
	
	/**
	 * Constructor of ElevatorController class.
	 *
	 * @Param port Port of Elevator
	 *
	 */
	public ElevatorController(int port) {
		inUse = false;
		requests = new ArrayList<int[]>();
		
		//Create DatagramSocket
		try {
	          sendReceiveSocket = new DatagramSocket();
	    } catch (SocketException se) {   // Can't create the socket.
	          se.printStackTrace();
	          System.exit(1);
	    }
		
		this.port = port;
		moving = false;
		
		//Elevators are initialized with floor 1, direction up, destination 1, 0 passengers
		elevatorInfo = new int[4];
		elevatorInfo[0]=1; //floor
		elevatorInfo[1]=1; //direction
		elevatorInfo[2]=1; //destination
		elevatorInfo[3]=0; //passengers
	}
	
	/**
	 * Get the port of ElevatorController's Elevator
	 * 
	 * @return Elevator's port
	 */
	public int getPort() {
		return port;
	}
	
	
	
	/**
	 * Fulfill a Passenger's request. Begin by calculating the direction to the passenger.
	 * Then pick up Passenger at the source floor, then travel to the destination floor. 
	 * 
	 * @param request The passenger's request [time, source, direction, destination]
	 */
	public void executeRequest(int[] request) {
		System.out.println("Starting Request");
		activeJob = request;
		inUse = true;
		
		if (elevatorInfo[0] != activeJob[1]) { //elevator already at src floor
			//Calculate the initial direction of the Elevator to reach the Passenger based on the request's source floor and the Elevator's current floor
			int direction = elevatorInfo[0]-activeJob[1] < 0 ? 1 : 0;
			
			elevatorInfo[1] = direction;//Update elevator direction
			elevatorInfo[2] = activeJob[1];//Update destination to request's source
			
			//Move Elevator to the source floor
			moveElevator(activeJob[1], direction);
			while (moving) {//Listen until the elevator reaches the source floor
				receiveControl();
			}// Elevator has arrived at source
		} else {
			System.out.println("Elevator " + port + " is already at the source floor: " + activeJob[1]);
		}
		
		//Prepare to move to destination
		elevatorInfo[1] = activeJob[2];//Update elevator direction
		elevatorInfo[2] = activeJob[3];//Update destination to request's destination
		
		//Move Elevator to destination floor
		moveElevator(activeJob[3],activeJob[2]);
		while (moving) {//Listen until elevator reaches destination
			//System.out.println("Listening...");
			receiveControl();
		}// Elevator has reached the destination
		inUse = false;
	}
	
	/**
	 * Gets inUse
	 * 
	 * @return True if the scheduler is working on a request
	 */
	public boolean getInUse() {
		return inUse;
	}
	
	/**
	 * Gets elevatorInfo
	 * 
	 * @return The elevator's [current floor, direction, dest floor,# passengers]
	 */
	public int[] getInfo() {
		return elevatorInfo;
	}
	
	/**
	 * Stops the Elevator's motor
	 */
	public void stopElevator() {
		sendControl((byte) 5);//Stop elevator's motor
		System.out.println(TimeConverter.msToTime(activeJob[0]) + ": Instructing elevator " + port + " to turn off motor.");
		moving = false;
		System.out.println(TimeConverter.msToTime(activeJob[0]) + ": Instructing elevator " + port + " to open door.");
		sendControl((byte) 0);//Open elevator's doors
	}
	
	/**
	 * Sets the direction and destination of the Elevator, and sets it in motion by turning on motor.
	 * 
	 * @param destination the destination floor
	 * @param direction 1 - up, 0 - down 
	 */
	public void moveElevator( int destination, int direction) {
		int newDirection = direction == 1 ? 2 : 3;//2: Direction up, 3: Direction Down
		System.out.println(TimeConverter.msToTime(activeJob[0]) + ": Instructing elevator " + port + " to close door.");
		sendControl((byte) 1); //close doors
		//sendControl((byte) (100+destination)); //set elevator destination
        String directionString = direction == 1 ? "up" : "down";
		System.out.println(TimeConverter.msToTime(activeJob[0]) + ": Instructing elevator " + port + " to go " + directionString);
		sendControl((byte) newDirection); //set elevator direction
		System.out.println(TimeConverter.msToTime(activeJob[0]) + ": Instructing elevator " + port + " to turn on motor \n");
		sendControl((byte) 4); //turn on motor
		moving = true;
	}

	/**
	 * Sends a control message to the Elevator
	 * 
	 * @param code Control message code 
	 */
	public void sendControl(byte code) {
		byte[] c = {code};
		//Create DatagramPacket with the control message
		try {
			sendPacket = new DatagramPacket(c, c.length, InetAddress.getLocalHost(), port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		        System.exit(1);
		}
		//Send DatagramSocket
		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
		        e.printStackTrace();
		        System.exit(1);
		}
	}
	
	/**
	 * Listen to sendreceiveSocket for UDP messages, then send the datagram's data to be decoded.
	 *  
	 */
	public void receiveControl() {
	      byte data[] = new byte[100];
	      receivePacket = new DatagramPacket(data, data.length);
	      // Block until a datagram packet is received from receiveSocket.
	      try {        
	         sendReceiveSocket.receive(receivePacket);
	      } catch (IOException e) {
	         System.out.print("IO Exception: likely:");
	         System.out.println("Receive Socket Timed Out.\n" + e);
	         e.printStackTrace();
	         System.exit(1);
	      }
	      decodeControl(receivePacket.getData());
	}
	
	/**
	 * Decodes the control message msg[] based on the first element of the array. Subsequence elements may hold additional data.
	 * 
	 * @param msg A control message
	 */
	public void decodeControl(byte[] msg) {
		int code = msg[0];
		switch (code) {
		case 6: //Elevator doors have opened
			elevatorInfo[1] = 1;
			break;
		case 7: //Elevator doors have closed
			elevatorInfo[1] = 0;
			break;
		case 8: //Elevator motor is powered on
			System.out.println("Motor on");
			break;
		case 9://Elevator motor is powered off
			System.out.println("Motor off");
			break;
		case 10://Elevator has set new destination
			System.out.println("Destination Set");
			break;
		case 11://Passenger has entered the elevator
			System.out.println("Passenger Entered");
			break;
		case 12://Passenger has left the elevator
			System.out.println("Passenger Left");
			break;
		case 13://Elevator has changed to a new floor
			System.out.println("elevator " + port + " arrived at floor " + msg[1] + ", destination: " + elevatorInfo[2]);
			elevatorInfo[0] = msg[1];//Update current floor
			if(elevatorInfo[0]==elevatorInfo[2]) { //Check if the elevator has reached its destination
				System.out.println("Stop");
				stopElevator(); // Elevator has reached destination
			} else {
				System.out.println("Continue");
				sendControl((byte) 15); // Do not stop
			}
			break;
		}
	}
	
	public void addRequest(int[] Request) {
		requests.add(Request);
	}
	
	@Override
	public void run() {
        while(true) {
        	//Insert code for execute request here
        	System.out.println(port + "Working...");
        	if (!requests.isEmpty()) {
        		executeRequest(requests.remove(0));
        	} else {
        		
        	}
        }
    }
}
