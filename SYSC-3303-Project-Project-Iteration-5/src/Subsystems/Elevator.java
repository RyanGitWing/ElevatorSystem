package Subsystems;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Elevator class that creates an elevator object which moves
 * from one floor to another based on the request. 
 *
 * @author Group2
 * @version April 12, 2022
 */
public class Elevator implements Runnable
{
	
	//faults disabled
	private Motor motor;
	
	private static final int TIME_BETWEEN_EACH_FLOOR = 300;//9500

	private static final int TIME_TO_OPEN_CLOSE = 100; //1000

	//Number of Elevators (must be consistent between Scheduler.java and Elevator.java
	private final static int NUMELEVATORS = 4;
	
	private int id, currentFloor, directionLamp, destination, sender, error;
	
	private boolean doorClosed, working;
	
	private DatagramPacket sendPacket;
	private DatagramPacket receivePacket;
	
	private DatagramSocket sendReceiveSocket;
	
	private LocalTime time = LocalTime.now();
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SS");
    private String fTime  = time.format(formatter);

	/**
	 * Elevator Constructor
	 * 
	 * @param id is the elevator ID.
	 */
	public Elevator(int id) 
	{
		this.id = id;
		currentFloor = 1;
		directionLamp = 1;
		doorClosed = false;
		working = true;
		motor = new Motor();   
		sender = 9823;
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
             System.out.print(fTime + " (Elevator): " + "IO Exception: likely:");
             System.out.println(fTime + " (Elevator): " + "Receive Socket Timed Out.\n" + e);
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
        byte[] reply = new byte[1];
		error = 1;//disable errors
        switch (code) 
        {
        case 0: //Elevator doors have opened
    		setDoor(false);
    		System.out.println(fTime + " (Elevator): " + "Elevator " + (id-4999) + " door opened!");
            break;
            
        case 1: //Elevator doors have closed
        	setDoor(true);
    		System.out.println(fTime + " (Elevator): " + "Elevator " + (id-4999) + " door closed!");
            break;
            
        case 2: //Set elevator direction up
        	setDirection(1);
            System.out.println(fTime + " (Elevator): " + "Elevator " + (id-4999) + " direction up");
            System.out.println(fTime + " (Elevator): " + "Elevator " + (id-4999) + " Direction Lamp: UP");
            break;
            
        case 3://Set elevator direction down
        	setDirection(0);
            System.out.println(fTime + " (Elevator): " + "Elevator " + (id-4999) + " direction down");
            System.out.println(fTime + " (Elevator): " + "Elevator " + (id-4999) + " Direction Lamp: DOWN");
            break;
            
        case 4://Turn elevator motor on
        	System.out.println(fTime + " (Elevator): " + "Elevator " + (id-4999) + " motor on");
        	turnOnMotor();
            break;
            
        case 20: //closes socket when elevator shuts down
        	sendReceiveSocket.disconnect();
        	working = false;
            break;
            
        case 21://elevator door is stuck open, self repair
        	try {
				System.out.println(fTime + " (Elevator): " + "Elevator " + (id-4999) + " door stuck, now repairing");
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	setDoor(true);
    		System.out.println(fTime + " (Elevator): " + "Elevator " + (id-4999) + " door closed!");
    		reply[0] = 6;
    		sendControl(reply);
            break;
            
        case 22://elevator door is stuck close, self repair
        	try {
				System.out.println(fTime + " (Elevator): " + "Elevator " + (id-4999) + " door stuck, now repairing");
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	setDoor(false);
    		System.out.println(fTime + " (Elevator): " + "Elevator " + (id-4999) + " door opened!");
    		reply[0] = 7;
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
			error = 1;//disable errors
			if ( error == 0 ) {
				motor.toggleMotor(false);
				return;
			}
			
			// Sleep the elevator thread for the time it takes to get to destination
			try {
				Thread.sleep(TIME_BETWEEN_EACH_FLOOR);
			} catch (InterruptedException e){
				
			}
			
			// Decrement the floor to simulate going down a floor level,
			// if the current floor is above the destination floor
			if (directionLamp == 0) 
			{
				currentFloor--;
				
			// else, do the opposite
			} else {
				currentFloor++;
			}
			
			// Display the elevator traversing each floor to get to destination floor
			System.out.println(fTime + " (Elevator): " + "Elevator " + (id-4999) + " approaching floor " + currentFloor);
			
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
	        	System.out.println(fTime + " (Elevator): " + "Elevator " + (id-4999) + " motor off");
	        	System.out.println(fTime + " (Elevator): " + "Elevator " + (id-4999) + " has arrived at floor " + currentFloor);
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
	 * Get the status of the elevator door.
	 * 
	 * @param
	 */
	public boolean getDoor(){
		return doorClosed;
	}
	
	/**
	 * Set the direction of the elevator.
	 * 
	 * @param direction is the direction the elevator will go.
	 */
	public void setDirection(int direction)
	{
		this.directionLamp = direction;
	}
	
	/**
	 * Get the direction the elevator is going.
	 * 
	 * @return The direction of the elevator either up (1) or down (0).
	 */
	public int getDirection() 
	{
		return directionLamp;
	}

	

	public int getError(){
		return error;
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
	 * @param args
	 */
	public static void main(String[] args) {

		for (int i = 0; i < NUMELEVATORS; i++) {
			Thread elevator1 = new Thread(new Elevator(5000+i));
			elevator1.start();
		}
	}
    
	@Override
	/**
	 * Runnable that runs the elevator thread.
	 */
	public void run() 
	{	
		while (working) 
		{	
			receiveControl();
		}
	}

}
