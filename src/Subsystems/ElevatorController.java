package Subsystems;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

/**
 * ElevatorController is in charge of controlling its Elevator to fulfill the request of the passenger.
 * ElevatorController communicates by receiving and sending  UDP messages to a well known port on it's Elevator.
 * 
 * @author Group2
 * @version March 27, 2022
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
	private boolean inUse, working;

	private ArrayList<int[]> floorsToVisit; // [Floor #, passengers in, passengers out]
	//{[1,1,0],[7,0,1],[3,1,0],[5,0,1]}
	
	/**
	 * Constructor of ElevatorController class.
	 *
	 * @Param port Port of Elevator
	 *
	 */
	public ElevatorController(int port) {
		inUse = false;
		requests = new ArrayList<int[]>();
		floorsToVisit = new ArrayList<int[]>();

		
		//Create DatagramSocket
		try {
	          sendReceiveSocket = new DatagramSocket();
	    } catch (SocketException se) {   // Can't create the socket.
	          se.printStackTrace();
	          System.exit(1);
	    }
		
		this.port = port;
		moving = false;
		working = true;
		
		//Elevators are initialized with floor 1, direction up, destination 1, 0 passengers
		elevatorInfo = new int[4];
		elevatorInfo[0]=1; //floor
		elevatorInfo[1]=1; //direction
		elevatorInfo[2]=1; //destination
		elevatorInfo[3]=0; //passengers
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
	public void receiveControl(boolean setTimeOut) {
	      byte data[] = new byte[100];
	      receivePacket = new DatagramPacket(data, data.length);
	      // Block until a datagram packet is received from receiveSocket.
	      try {     
	    	 if (setTimeOut) {
		         sendReceiveSocket.setSoTimeout(3000);
	    	 }
	         sendReceiveSocket.receive(receivePacket);
	      } catch (SocketTimeoutException se) {
	    	  System.out.println(TimeConverter.msToTime(activeJob[0]) + ": Scheduler has detected that Elevator " + (port-4999) + " has exceeded the expected travel time. The elevator has been disabled.\n");
	    	  sendControl((byte) 20);
	    	  working = false;
	    	  return;
	      } catch (IOException e) {
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
			if (msg[1] == 1) {
				try {
					System.out.println(TimeConverter.msToTime(activeJob[0]) + ": Door stuck, now repairing elevator " + (port-4999) + " door");
					Thread.sleep(2000);
					sendControl((byte) 0);
					receiveControl(false);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println(TimeConverter.msToTime(activeJob[0]) + ": Elevator " + (port-4999) + " Door open");
			}
			break;
		case 7: //Elevator doors have closed
			if (msg[1] == 1) {
				try {
					System.out.println(TimeConverter.msToTime(activeJob[0]) + ": Door stuck, now repairing elevator " + (port-4999) + " door");
					Thread.sleep(2000);
					sendControl((byte) 1);
					receiveControl(false);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println(TimeConverter.msToTime(activeJob[0])  + ": Elevator " + (port-4999) + " Door close");
			}
			break;
		case 8: //Elevator motor is powered on
			System.out.println(TimeConverter.msToTime(activeJob[0])  + ": Elevator " + (port-4999) + " Motor on");
			break;
		case 9://Elevator motor is powered off
			System.out.println(TimeConverter.msToTime(activeJob[0])  + ": Elevator " + (port-4999) + " Motor off");
			break;
		case 10://Elevator has set new destination
			System.out.println(TimeConverter.msToTime(activeJob[0])  + ": Elevator " + (port-4999) + " Destination Set");
			break;
		case 11://Passenger has entered the elevator
			System.out.println(TimeConverter.msToTime(activeJob[0])  + ": Elevator " + (port-4999) + " Passenger Entered");
			break;
		case 12://Passenger has left the elevator
			System.out.println(TimeConverter.msToTime(activeJob[0])  + ": Elevator " + (port-4999) + " Passenger Left");
			break;
		case 13://Elevator has changed to a new floor
			System.out.println(TimeConverter.msToTime(activeJob[0]) + ": Elevator " + (port-4999) + " arrived at floor " + msg[1] + ", destination: " + elevatorInfo[2]);
			elevatorInfo[0] = msg[1];//Update current floor
			if(elevatorInfo[0]==elevatorInfo[2]) { //Check if the elevator has reached its destination
				System.out.println(TimeConverter.msToTime(activeJob[0])  + ": Elevator " + (port-4999) + " Stop");
				stopElevator(); // Elevator has reached destination
			} else {
				System.out.println(TimeConverter.msToTime(activeJob[0])  + ": Elevator " + (port-4999) + " Continue");
				sendControl((byte) 15); // Do not stop
			}
			break;
		}
	}
	
	/**
	 * Stops the Elevator's motor
	 */
	public void stopElevator() {
		sendControl((byte) 5);//Stop elevator's motor
		System.out.println(TimeConverter.msToTime(activeJob[0]) + ": Instructing elevator " + (port-4999) + " to turn off motor.");
		moving = false;
		System.out.println(TimeConverter.msToTime(activeJob[0]) + ": Instructing elevator " + (port-4999) + " to open door.");
		sendControl((byte) 0);//Open elevator's doors
		receiveControl(false);
		if (floorsToVisit.isEmpty()) {
			inUse = false;
		}
	}
	
	/**
	 * Sets the direction and destination of the Elevator, and sets it in motion by turning on motor.
	 * 
	 * @param destination the destination floor
	 * @param direction 1 - up, 0 - down 
	 */
	public void moveElevator(int direction) {
		int newDirection = direction == 1 ? 2 : 3;//2: Direction up, 3: Direction Down
		System.out.println(TimeConverter.msToTime(activeJob[0]) + ": Instructing elevator " + (port-4999) + " to close door.");
		sendControl((byte) 1); //close doors
		receiveControl(false);
		//sendControl((byte) (100+destination)); //set elevator destination
        String directionString = direction == 1 ? "up" : "down";
		System.out.println(TimeConverter.msToTime(activeJob[0]) + ": Instructing elevator " + (port-4999) + " to go " + directionString);
		sendControl((byte) newDirection); //set elevator direction
		System.out.println(TimeConverter.msToTime(activeJob[0]) + ": Instructing elevator " + (port-4999) + " to turn on motor \n");
		sendControl((byte) 4); //turn on motor
		moving = true;
	}
	
	/**
	 * Adds requests to ArrayList that contains the floor information.
	 *  
	 *  @param request The passenger's request [time, source, direction, destination]
	 */
	public synchronized void addRequest(int[] request) {
		activeJob = request;
		int[] pickUp = new int[3];
		pickUp[0] = request[1];
		pickUp[1] = 1;
		pickUp[2] = 0;
		floorsToVisit.add(pickUp);
		
		int[] dropOff = new int[3];
		dropOff[0] = request[3];
		dropOff[1] = 0;
		dropOff[2] = 1;
		floorsToVisit.add(dropOff);
		notifyAll();
	}
	
	/**
	 * Gets first request from list of all requests
	 * @return first request
	 */
	public synchronized int[] getRequest() {
		while (requests.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		
		return requests.remove(0);
	}
	
	/**
	 * Determines whether the elevator is currently in use. This is determined
	 * by checking if there are requests to be handled.
	 */
	public synchronized void setInUse() {
    	if (floorsToVisit.isEmpty()) {
    		inUse = false;
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.println(e);
			}
    	} else {
    		inUse = true;
    	}
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
	 * Returns floor information for specified request
	 * @param i index of floor location
	 * @return floor location and passenger info
	 */
	public synchronized int[] getFloorsToVisit(int i) {
		int[] stop = floorsToVisit.get(i);
		return stop;
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
	 * Gets elevatorInfo
	 * 
	 * @return The elevator's [current floor, direction, dest floor,# passengers]
	 */
	public int[] getInfo() {
		return elevatorInfo;
	}
	
	@Override
	public void run() {
        while(working) {
        	setInUse();
        	while(inUse) {
        		if (moving) {
        			receiveControl(true);
        			if (working == false) {
    					return;
        			}
        		}
        		else { //Elevator is Stop but has jobs to do
        			//Src + Dst, *Src + *dst, src + *dst, Dst
        			//check pickUpFloors
            		int src = floorsToVisit.remove(0)[0];
            		if (elevatorInfo[0] != src) { //elevator not at src floor of the 1st request
            			//Calculate the initial direction of the Elevator to reach the Passenger based on the request's source floor and the Elevator's current floor
            			int direction = elevatorInfo[0]-src < 0 ? 1 : 0;//1 - up, 0 - down
            			
            			elevatorInfo[1] = direction;//Update elevator direction
            			elevatorInfo[2] = src;//Update destination to request's source
            			
            			//Move Elevator to the source floor
            			moveElevator(direction);
        			}
        		}        	
        	//executeRequest(getRequest());
        	}
        }
	}
	
	/**
	 * CURRENTLY UNUSED- MAY BE NEEDED FOR FUTURE REFERENCE
	 * Fulfill a Passenger's request. Begin by calculating the direction to the passenger.
	 * Then pick up Passenger at the source floor, then travel to the destination floor. 
	 * 
	 * @param request The passenger's request [time, source, direction, destination]
	 *
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
				receiveControl(true);
				if (working == false) {
					return;
				}
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
			receiveControl(true);
			if (working == false) {
				return;
			}
		}// Elevator has reached the destination
		inUse = false;
	}
	*/
}
