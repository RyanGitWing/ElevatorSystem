import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * The FloorRequestHandler class receives UDP packets from
 * the floor subsystem with the request and stores it in a list.
 * It provides a method to access the first request in the list.
 * 
 * @author Aleksandar Veselinovic
 * @version March 12, 2022
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
	 * This method receives a request from the floor
	 * subsystem in a UDP packet and stores it in
	 * a list.
	 */
	public synchronized void receiveRequest() {
		//receive packet
		byte data[] = new byte[100];
		receivePacket = new DatagramPacket(data, data.length);
	    
		try { 
			receiveSocket.receive(receivePacket);
	    } catch (IOException e) {
	    	e.printStackTrace();
	    	System.exit(1);
	    }
		
		//convert UDP data into request
		String requestString = new String(data, 0, receivePacket.getLength());
        String[] splitString = requestString.split(" ");
        
        int convertedTime = TimeConverter.timeToMS(splitString[0]);	            
        
        int[] request = new int[4];
        
        request[0] = convertedTime;                          //time in ms
        request[1] = Integer.parseInt(splitString[1]);       //source floor
        request[2] = splitString[2].equals("Up") ? 1 : 0;    //direction of elevator (up = 1, down = 0)
        request[3] = Integer.parseInt(splitString[3]);       //destination floor
        
        //wait until the request list is empty to add the request to the list
        while (!requests.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
        requests.add(request);
        String direction = request[2] == 1 ? "up" : "down";
        System.out.println("Scheduler received request:\n" + TimeConverter.msToTime(request[0]) + ": From floor " + request[1] + " to go " + direction + " to floor " + request[3] + ".\n");

		notifyAll();
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
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		notifyAll();
		return requests.remove(0);
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