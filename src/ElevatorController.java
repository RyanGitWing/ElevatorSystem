import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

/**
 * 
 */

/**
 * @author Dom
 *
 */
public class ElevatorController implements Runnable {

	private ArrayList<int[]> requests; //[time, source, dir, dest]
	private int[] activeJob;
	private DatagramSocket sendReceiveSocket;
	private DatagramPacket receivePacket, sendPacket;
	private int port;
	private boolean moving;
	//elevatorInfo = [current floor, direction, dest floor,# passengers]
	private int[] elevatorInfo;
	
	/**
	 * 
	 */
	public ElevatorController(int port) {
		requests = new ArrayList<int[]>();
		try {
	          sendReceiveSocket = new DatagramSocket();
	    } catch (SocketException se) {   // Can't create the socket.
	          se.printStackTrace();
	          System.exit(1);
	    }
		this.port = port;
		moving = false;
		
		elevatorInfo = new int[4];
		elevatorInfo[0]=1;
		elevatorInfo[1]=1;
		elevatorInfo[2]=1;
		elevatorInfo[3]=0;
	}
	
	public void addRequest(int[] request) {
		requests.add(request);
	}
	
	public int[] getInfo() {
		return elevatorInfo;
	}
	
	public void stopElevator() {
		sendControl((byte) 5);//Stop elevator's motor
		moving = false;
		sendControl((byte) 0);//Open elevator's doors
		
	}
	
	public void moveElevator( int destination, int direction) {
		int newDirection = direction == 1 ? 2 : 3;//2: Direction up, 3: Direction Down
		sendControl((byte) 1); //close doors
		sendControl((byte) (100+destination)); //set elevator destination
		sendControl((byte) newDirection); //set elevator direction
		sendControl((byte) 5); //turn on motor
		moving = true;
	}

	
	public void sendControl(byte code) {
		byte[] c = {code};
		try {
			sendPacket = new DatagramPacket(c, c.length, InetAddress.getLocalHost(), port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		        System.exit(1);
		}

		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
		        e.printStackTrace();
		        System.exit(1);
		}
	}
	
	public void receiveControl() {
	      byte data[] = new byte[100];
	      receivePacket = new DatagramPacket(data, data.length);
	      System.out.println("Intermediate: Waiting for Packet.");

	      // Block until a datagram packet is received from receiveSocket.
	      try {        
	         System.out.println("Waiting..."); // so we know we're waiting
	         sendReceiveSocket.receive(receivePacket);
	      } catch (IOException e) {
	         System.out.print("IO Exception: likely:");
	         System.out.println("Receive Socket Timed Out.\n" + e);
	         e.printStackTrace();
	         System.exit(1);
	      }
	      System.out.println("Received");
	      decodeControl(receivePacket.getData());
	}
	
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
			elevatorInfo[0] = msg[1];//Update current floor
			if(elevatorInfo[0]==elevatorInfo[2]) { stopElevator();} //The elevator has reached its destination
			break;
		}
	}	
	
	@Override
	public void run() {
		System.out.println("ElevatorController: Controlling elevator on port: " + port);
		while(true) {
			if (!requests.isEmpty()) {
				activeJob = requests.get(0);
				
				int direction = elevatorInfo[1]-activeJob[1] < 0 ? 1 : 0;
				
				elevatorInfo[1] = direction;//Update elevator direction
				elevatorInfo[2] = activeJob[1];//Update destination to request's src
				
				//Move to src
				moveElevator(activeJob[1], direction);
				while (moving) {//Listen until the elevator reaches the src
					receiveControl();
				}
				elevatorInfo[1] = activeJob[2];//Update elevator direction
				elevatorInfo[2] = activeJob[3];//Update destination to request's dst
				
				//Move to dst
				moveElevator(activeJob[3],activeJob[2]);
				while (moving) {//Listen until elevator reaches dst
					receiveControl();
				}
				requests.remove(0);
			}
		}
	}
}
