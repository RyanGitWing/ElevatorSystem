import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * The floor class is used to simulate the arrival of passengers
 * to the elevators and simulating buttons being pressed.
 *
 * @author Tyler Leung
 * @version February 19th, 2022
 */
public class Floor implements Runnable{
    
	private DatagramPacket sendPacket;	
	private DatagramSocket controlSocket;
	

    /**
     * Floor constructor that takes in a scheduler object.
     * This allows for any floors to utilize the scheduler.
     * 
     * @param scheduler Scheduler object used to put instructions 
     */
    public Floor(){
        try {
            controlSocket = new DatagramSocket();
        } catch (SocketException se) {   
            se.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * This method reads in an input file and parses through it keeping
     * only the important information saved. This information is then 
     * added to an array that stores the time, the source floor, the direction
     * and the destination floor. This is then added to an array list which
     * stores each line from the file individually to simulate different instructions.
     * 
     * @param inputFile File containing instructions for elevators
     * @return ArrayList containing all instructions from file
     */
    public void readInput(File inputFile){
    	
        //ArrayList containing the parsed instructions
        ArrayList<int[]> parsedInput = new ArrayList<int[]>();
        
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
	        	//create datagram
	        	try {
	    			sendPacket = new DatagramPacket(msg, msg.length, InetAddress.getLocalHost(), 4999);
	    		} catch (UnknownHostException e) {
	    			e.printStackTrace();
	    	        System.exit(1);
	    		}
	        	
	        	//Send Datagram
	        	System.out.println("Sending Request");
	        	try {
					controlSocket.send(sendPacket);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
	        	
	        }

            //closes scanner to stop any possible memory leak
			scanner.close();
	        
		} catch (FileNotFoundException e) {
            
            //Prints if there is no file found
			System.out.println("File not found.");
		
        }
    }

    /**
     * On thread start, this method reads in the information from the selected input file.
     * It then sends it to the scheduler using a sychronized put method.
     * If there is an instruction within the scheduler, it receives it and prints out 
     * the values from the instruction.
     */
    @Override
    public void run(){
    	readInput(new File("src\\inputFile.txt"));
    }
    
    public static void main(String[] args) {
    	Thread f = new Thread(new Floor());
    	f.start();
    }
}
