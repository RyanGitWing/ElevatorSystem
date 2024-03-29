package Subsystems;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * The FloorRequestHandler class receives UDP packets from
 * the floor subsystem with the request and stores it in a list.
 * It provides a method to access the first request in the list.
 * 
 * @author Group2
 * @version April 12, 2022
 */
public class FloorRequestHandler implements Runnable {

	private DatagramSocket receiveSocket;
	private DatagramPacket receivePacket;
	private ArrayList<int[]> requests;
    
	/**
	 * Constructor of a Port object. Binds the DatagramSocket
	 * to the specified port.
	 * 
	 * @param port the port number to bind the socket to
	 */
	public FloorRequestHandler(int port) {
		requests = new ArrayList<int[]>();
		try {
			receiveSocket = new DatagramSocket(port);
		} catch (SocketException se) {
			se.printStackTrace();
	        System.exit(1);
		}
	}

	/**
	 * Adds requests to ArrayList that contains the floor information.
	 *  
	 *  @param request The passenger's request [time, source, direction, destination]
	 */
	public synchronized void addRequest(int[] request) {
    	LocalTime time = LocalTime.now();
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SS");
        String fTime  = time.format(formatter);
        
	    //wait until the request list is empty to add the request to the list
        while (!requests.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
        requests.add(request);
        String direction = request[2] == 1 ? "up" : "down";
        System.out.println(fTime + " (Scheduler -> FloorRequestHandler)" + ": Scheduler received request:" + " From floor " + request[1] + " to go " + direction + " to floor " + request[3] + ".\n");
		notifyAll();
		
	}
	
	/**
	 * This method receives a request from the floor
	 * subsystem in a UDP packet and stores it in
	 * a list.
	 */
	public void receiveRequest() {
		//receive packet
		byte data[] = new byte[100];
		receivePacket = new DatagramPacket(data, data.length);
	    
		try { 
			receiveSocket.receive(receivePacket);
	    } catch (IOException e) {
	    	e.printStackTrace();
	    	return;
	    }
		
		//convert UDP data into request
		String requestString = new String(data, 0, receivePacket.getLength());
        String[] splitString = requestString.split(" ");
        
        int convertedTime = TimeConverter.timeToMS(splitString[0]);	            
        
        int[] request = new int[5];
        
        request[0] = convertedTime;                          //time in ms
        request[1] = Integer.parseInt(splitString[1]);       //source floor
        request[2] = splitString[2].equals("Up") ? 1 : 0;    //direction of elevator (up = 1, down = 0)
        request[3] = Integer.parseInt(splitString[3]);       //destination floor
        request[4] = Integer.parseInt(splitString[4]);       //type of fault
        
    	LocalTime time = LocalTime.now();
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SS");
        String fTime  = time.format(formatter);
        
        if(request[1] > 0 && request[1] < 23) {
        	if(request[3] > 0 && request[3] < 23) {
        		addRequest(request);
        	} else {
        		System.out.println(fTime + " (Scheduler -> FloorRequestHandler): " + "Invalid destination floor!");
        	}
        } else {
        	System.out.println(fTime + " (Scheduler -> FloorRequestHandler): " + "Invalid source floor!");
        }
	}
	
	/**
	 * This method removes and returns the first
	 * request from the request list.
	 * 
	 * @return the request removed from the list
	 */
	public synchronized int[] getRequest() {
		//wait until there is a request in the list
		while (requests.isEmpty()) {
			int[] empty = {-1};
			return empty;
		}
		int request[] = requests.remove(0);
		notifyAll();
		return request;
	}

	/**
	 * When the thread runs it continually waits
	 * for UDP packets from the floor subsystem.
	 */
	public void run() {
		while (true) {
			receiveRequest();
		}
	}
}
