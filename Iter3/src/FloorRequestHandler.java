import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class FloorRequestHandler implements Runnable {

	private DatagramSocket receiveSocket;
	private DatagramPacket receivePacket;
	private ArrayList<int[]> requests;

	public FloorRequestHandler(int port) {
		requests = new ArrayList<int[]>();
		try {
			receiveSocket = new DatagramSocket(port);
		} catch (SocketException se) {
			se.printStackTrace();
	        	System.exit(1);
		}
	}

	public synchronized void receiveRequest() {
		byte data[] = new byte[100];
		receivePacket = new DatagramPacket(data, data.length);
	    
		try { 
			receiveSocket.receive(receivePacket);
	       } catch (IOException e) {
		          e.printStackTrace();
		          System.exit(1);
	       }
		String requestString = new String(data, 0, receivePacket.getLength());
        String[] splitString = requestString.split(" ");
        
        int convertedTime = TimeConverter.timeToMS(splitString[0]);	            
        
        int[] request = new int[4];
        
        request[0] = convertedTime;                          //time in ms
        request[1] = Integer.parseInt(splitString[1]);       //source floor
        request[2] = splitString[2].equals("Up") ? 1 : 0;    //direction of elevator (up = 1, down = 0)
        request[3] = Integer.parseInt(splitString[3]);       //destination floor
        
        requests.add(request);
        System.out.println("Request Added");
        System.out.println(request);
        System.out.print("Sending request to scheduler at " + TimeConverter.msToTime(request[0]));
        String direction = request[2] == 1 ? "up" : "down";
        System.out.print(" from floor " + request[1] + " to go " + direction + " to floor: " + request[3] + ".\n");

		notifyAll();
	}

	
	
	
	public synchronized int[] getRequest() {
		if (requests.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {

			}
		}
		return requests.get(0);
	}

	public void run() {
		while (true) {
			System.out.println("RequestHandler: Listening for requests");
			receiveRequest();
		}
	}
}