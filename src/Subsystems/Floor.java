package Subsystems;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.IOException;
import java.net.*;

/**
 * The floor class is used to simulate the arrival of passengers
 * to the elevators and simulating buttons being pressed.
 *
 * @author Group2
 * @version March 27, 2022
 */
public class Floor implements Runnable {
    
	private DatagramPacket sendPacket;	
	private DatagramSocket controlSocket;
	
    /**
     * Floor constructor.
     * 
     */
    public Floor(){
        try {
        	//Create socket
            controlSocket = new DatagramSocket();
        } catch (SocketException se) {   
            se.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * This method reads in an input file and scan each line.
     * Each line is converted into a byte array and sent to the floor
     * request handler through a datagramPacket.
     * 
     * @param inputFile File containing instructions for elevators
     */
    public void readInput(File inputFile)
    {
		try {

            //Scanner object to read the input file
	        Scanner scanner = new Scanner(inputFile);
	        	        	        
	        //Keeps reading while there is information on the next line
	        while(scanner.hasNextLine())
	        {
	        	String line = scanner.nextLine();
	        	
	        	int len = line.length();
	        	
	            byte[] msg = new byte[len];
	            
	        	msg = line.getBytes();
	        	
	        	//Create datagram
	        	try {
	    			sendPacket = new DatagramPacket(msg, msg.length, InetAddress.getLocalHost(), 4999);
	    		} catch (UnknownHostException e) {
	    			e.printStackTrace();
	    	        System.exit(1);
	    		}
	        	
	        	//Send datagram to floor request handler
	        	System.out.println("Sending Request: " + line);
	        	try {
					controlSocket.send(sendPacket);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
	        	
	        }

            //Closes scanner to stop any possible memory leak
			scanner.close();
	        
		} catch (FileNotFoundException e) {
            
            //Prints if there is no file found
			System.out.println("File not found.");
		
        }
    }
    
    /**
     * Main function to start the floor subsystem.
     * 
     * @param args
     */
    public static void main(String[] args) {
    	Thread f = new Thread(new Floor());
    	f.start();
    }

    /**
     * On thread start, this method reads the input file.
     */
    @Override
    public void run(){
    	readInput(new File(".\\Documents\\inputFile.txt"));
    }
}
