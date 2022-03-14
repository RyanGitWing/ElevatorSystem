import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Elevator class that creates an elevator object which moves
 * from one floor to another based on the request. 
 *
 * @author Harrison Lee
 * @version March 12, 2022
 */
public class Elevator implements Runnable
{
	private Motor motor;
	
	private static final int TIME_BETWEEN_EACH_FLOOR = 3000;

	private static final int TIME_TO_OPEN_CLOSE = 1000; 
	
	private int id, currentFloor, direction, destination, sender;
	
	private boolean doorClosed;
	
	private DatagramPacket sendPacket;
	private DatagramPacket receivePacket;
	
	private DatagramSocket sendReceiveSocket;

	/**
	 * Elevator Constructor
	 * 
	 * @param id is the elevator ID.
	 */
	public Elevator(int id) 
	{
		this.id = id;
		currentFloor = 1;
		direction = 1;
		doorClosed = false;
		motor = new Motor();                        

	     try {
	    	 sendReceiveSocket = new DatagramSocket(id);
	     } catch (SocketException se) {   
	     	se.printStackTrace();
	     	System.exit(1);
	     }			
	}
	
	/**
	 * Creates and sends datagram packets to send to the elevator controller
	 * 
	 * @param code is a list of bytes that is used to send messages between the elevator and elevator controller
	 */
	public void sendControl(byte code[]) {
        try {
            sendPacket = new DatagramPacket(code, code.length, InetAddress.getLocalHost(), sender);
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
    
	/**
	 * Receives datagram packets from the elevator controller
	 * 
	 * @param 
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
          sender = receivePacket.getPort();
          decodeControl(data);
    }
    
    /**
	 * Determines what the elevator will do depending on the code(instructions) it was sent  
	 * 
	 * @param msg is an array of bytes that is received from the controller
	 */
    public void decodeControl(byte[] msg) {
        int code = msg[0];
        switch (code) {
        case 0: //Elevator doors have opened
        	setDoor(!doorClosed);
    		System.out.println("Elevator " + id + " door opened!");
            break;
        case 1: //Elevator doors have closed
            setDoor(doorClosed);
    		System.out.println("Elevator " + id + " door closed!");
            break;
        case 2: //Set elevator direction up
        	setDirection(1);
            System.out.println("Elevator " + id + " direction up");
            break;
        case 3://Set elevator direction down
        	setDirection(0);
            System.out.println("Elevator " + id + " direction down");
            break;
        case 4://Turn elevator motor on
        	System.out.println("Elevator " + id + " motor on");
        	turnOnMotor();
            break;
        }
    }
	
	/**
	 * Turns on the elevator motor.
	 * Elevator will move between floors until it reaches
	 * the desired destination floor.
	 */
	public void turnOnMotor() 
	{
		// Turn on elevator motor
		motor.toggleMotor(true);
		
		// Will keep moving until reaches destination floor
		while (motor.getOn()) 
		{
			int temp = currentFloor - destination;
			int distanceBetweenFloor = Math.abs(temp);
			
			// Sleep the elevator thread for the time it takes to get to destination
			try {
				Thread.sleep(TIME_BETWEEN_EACH_FLOOR * distanceBetweenFloor);
			} catch (InterruptedException e){
				
			}
			
			// Decrement the floor to simulate going down a floor level,
			// if the current floor is above the destination floor
			if (direction == 0) 
			{
				currentFloor--;
				
			// else, do the opposite
			} else {
				currentFloor++;
			}
			
			// Display the elevator traversing each floor to get to destination floor
			System.out.println("Elevator " + id + " approaching floor " + currentFloor);
			
			//Sends current floor data to controller 
			byte arr[] = new byte[2];
			arr[0] = (byte) 13;
			arr[1] = (byte) currentFloor;
			sendControl(arr);
			
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
	        if (data[0] == 5) {
	        	System.out.println("Elevator " + id + " motor off");
	        	System.out.println("Elevator " + id + " has arrived at floor " + currentFloor);
	        	motor.toggleMotor(false);
	        }
		}

	}
	
	/**
	 * Open/Close the door based on request.
	 * 
	 * @param closeDoor closes/opens the elevator door.
	 */
	public void setDoor(boolean closeDoor) 
	{
		
		// Sleep the elevator thread for the amount
		// of time it takes to open/close door
		try {
			Thread.sleep(TIME_TO_OPEN_CLOSE);
		} catch (InterruptedException e) {
			
		}
		
		doorClosed = closeDoor;
		
	}
	
	/**
	 * Get the elevator ID.
	 * 
	 * @return The elevator ID.
	 */
	public int getElevatorID() 
	{	
		return this.id;
	}
	
	/**
	 * Get the current floor that the elevator is at.
	 * 
	 * @return The current floor of the elevator.
	 */
	public int getCurrentFloor() 
	{
		return currentFloor;
	}
	
	/**
	 * Get the direction the elevator is going.
	 * 
	 * @return The direction of the elevator either up (1) or down (0).
	 */
	public int getDirection() 
	{
		return direction;
	}

	/**
	 * Set the direction of the elevator.
	 * 
	 * @param direction is the direction the elevator will go.
	 */
	public void setDirection(int direction)
	{
		this.direction = direction;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int numberOfElevators = 1;
		for (int i = 0; i < numberOfElevators; i++) {
			Thread elevator1 = new Thread(new Elevator(5000+i));
			elevator1.start();
		}
		/*Thread elevator1 = new Thread(new Elevator(5000));
		Thread elevator2 = new Thread(new Elevator(5001));
		Thread elevator3 = new Thread(new Elevator(5002));
		Thread elevator4 = new Thread(new Elevator(5003));
	    
		elevator1.start();
		elevator2.start();
		elevator3.start();
		elevator4.start();*/
	}
    
	@Override
	/**
	 * Runnable that runs the elevator thread.
	 */
	public void run() 
	{	
		while (true) 
		{	
			receiveControl();
		}
	}

}
