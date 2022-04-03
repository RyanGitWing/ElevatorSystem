package Subsystems;
import java.io.IOException;
import java.net.*;
import java.util.*; 
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * ElevatorController is in charge of controlling its Elevator to fulfill the request of the passenger.
 * ElevatorController communicates by receiving and sending  UDP messages to a well known port on it's Elevator.
 * 
 * @author Group2
 * @version April 12, 2022
 */
public class ElevatorController implements Runnable {

	//Request Queue
	private ArrayList<int[]> requests; //[time, source, dir, dest]
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
	private boolean doorStuckFault, elevatorStuckFault;
	private int fault;
	private ArrayList<Integer> faultList;
	private ArrayList<int[]> floorsToVisit; // [Floor #, passengers in, passengers out, fault]
	
	private LocalTime time = LocalTime.now();
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SS");
    private String fTime  = time.format(formatter);
	
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
		faultList = new ArrayList<Integer>();
		fault = 0;
		
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
		elevatorInfo = new int[5];
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
	public void receiveControl() {
	      byte data[] = new byte[100];
	      receivePacket = new DatagramPacket(data, data.length);
	      // Block until a datagram packet is received from receiveSocket.
	      try {
	    	  sendReceiveSocket.receive(receivePacket);
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
			System.out.println(fTime + " (Scheduler -> Elevator Controller)" + ": Elevator " + (port-4999) + " Door open");
			fault = 0;
			break;
		case 7: //Elevator doors have closed
			System.out.println(fTime  + " (Scheduler -> Elevator Controller)" + ": Elevator " + (port-4999) + " Door close");
			fault = 0;
			break;
		case 8: //Elevator motor is powered on
			System.out.println(fTime + " (Scheduler -> Elevator Controller)" + ": Elevator " + (port-4999) + " Motor on");
			break;
		case 9://Elevator motor is powered off
			System.out.println(fTime + " (Scheduler -> Elevator Controller)" + ": Elevator " + (port-4999) + " Motor off");
			break;
		case 10://Elevator has set new destination
			System.out.println(fTime + " (Scheduler -> Elevator Controller)" + ": Elevator " + (port-4999) + " Destination Set");
			break;
		case 11://Passenger has entered the elevator
			System.out.println(fTime + " (Scheduler -> Elevator Controller)" + ": Elevator " + (port-4999) + " Passenger Entered");
			break;
		case 12://Passenger has left the elevator
			System.out.println(fTime + " (Scheduler -> Elevator Controller)" + ": Elevator " + (port-4999) + " Passenger Left");
			break;
		case 13://Elevator has changed to a new floor
			System.out.println(fTime + " (Scheduler -> Elevator Controller)" + ": Arrival sensor has detected Elevator " + (port-4999) + " at floor " + msg[1] + ", destination: " + elevatorInfo[2]);
			elevatorInfo[0] = msg[1];//Update current floor
			if(elevatorInfo[0]==elevatorInfo[2]) { //Check if the elevator has reached its destination
				System.out.println(fTime + " (Scheduler -> Elevator Controller)" + ": Elevator " + (port-4999) + " Stop");
				System.out.println(fTime + " (Scheduler -> Elevator Controller)" + ": Floor " + elevatorInfo[2] + " lamp turned off");
				stopElevator(); // Elevator has reached destination
			} else {
				System.out.println(fTime + " (Scheduler -> Elevator Controller)" + ": Elevator " + (port-4999) + " Continue");
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
		System.out.println(fTime + " (Scheduler -> Elevator Controller)" + ": Instructing elevator " + (port-4999) + " to turn off motor.");
		moving = false;
		System.out.println(fTime + " (Scheduler -> Elevator Controller)" + ": Instructing elevator " + (port-4999) + " to open door.");
		sendControl((byte) 0);//Open elevator's doors
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
		System.out.println(fTime + " (Scheduler -> Elevator Controller)" + ": Instructing elevator " + (port-4999) + " to close door.");
		if (doorStuckFault) {
			sendControl((byte) 21);//close doors fault
			fault = 1;
			receiveControl();
		} else {
			sendControl((byte) 1); //close doors
		}

        String directionString = direction == 1 ? "up" : "down";
		System.out.println(fTime + " (Scheduler -> Elevator Controller)" + ": Instructing elevator " + (port-4999) + " to go " + directionString);
		sendControl((byte) newDirection); //set elevator direction
		System.out.println(fTime + " (Scheduler -> Elevator Controller)" + ": Instructing elevator " + (port-4999) + " to turn on motor \n");
		sendControl((byte) 4); //turn on motor
		moving = true;
	}
	
	/**
	 * Adds requests to ArrayList that contains the floor information.
	 *  
	 *  @param request The passenger's request [time, source, direction, destination]
	 */
	public synchronized void addRequest(int[] request) {
		int[] pickUp = new int[4];
		pickUp[0] = request[1];
		pickUp[1] = 1;
		pickUp[2] = 0;
		pickUp[3] = 0;
		floorsToVisit.add(pickUp);
		
		int[] dropOff = new int[4];
		dropOff[0] = request[3];
		dropOff[1] = 0;
		dropOff[2] = 1;
		dropOff[3] = request[4];
		floorsToVisit.add(dropOff);
		
		faultList.add(request[4]);
		faultList.add(request[4]);
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
	
	/**
	 * @return
	 */
	public int getFault() {
		return fault;
	}
	
	/**
	 * @return
	 */
	public boolean getMoving() {
		return moving;
	}
	
	@Override
	public void run() {
        while(working) {
        	setInUse();
        	while(inUse) {
        		if (moving) {
        			receiveControl();
        			if (working == false) {
    					return;
        			}
        		}
        		else { //Elevator is Stop but has jobs to do
        			//Src + Dst, *Src + *dst, src + *dst, Dst
        			//check pickUpFloors
        			int[] floor = floorsToVisit.remove(0);
            		int src = floor[0];
            		doorStuckFault = floor[3] == 1 ? true : false;
            		elevatorStuckFault = floor[3] == 2 ? true : false;
            		if (elevatorStuckFault) {
            			fault = 2;
            			sendControl((byte) 20);
            			return;
            		}
            		if (elevatorInfo[0] != src) { //elevator not at src floor of the 1st request
            			//Calculate the initial direction of the Elevator to reach the Passenger based on the request's source floor and the Elevator's current floor
            			int direction = elevatorInfo[0]-src < 0 ? 1 : 0;//1 - up, 0 - down
            			
            			elevatorInfo[1] = direction;//Update elevator direction
            			elevatorInfo[2] = src;//Update destination to request's source
            			
            			//Move Elevator to the source floor
            			moveElevator(direction);
        			}
        		}        	
        	}
        }
	}
}
