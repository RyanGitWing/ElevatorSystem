import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The floor class is used to simulate the arrival of passengers
 * to the elevators and simulating buttons being pressed.
 *
 * @author tleung
 */
public class Floor implements Runnable{
    
    //Scheduler object
    private Scheduler scheduler;

    /**
     * Floor constructor that takes in a scheduler object.
     * This allows for any floors to utilize the scheduler.
     * 
     * @param scheduler Scheduler object used to put instructions 
     */
    public Floor(Scheduler scheduler){
    	
        //Set scheduler to this classes scheduler
        this.scheduler = scheduler;
        
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
    public ArrayList<int[]> readInput(File inputFile){
    	
        //ArrayList containing the parsed instructions
        ArrayList<int[]> parsedInput = new ArrayList<int[]>();
        
		try {

            //Scanner object to read the input file
	        Scanner scanner = new Scanner(inputFile);
	        	        	        
	        //Keeps reading while there is information on the next line
	        while(scanner.hasNextLine())
	        {
	        	
                //Array to contain the information from the line
	            int[] storedInstructions = new int[4];
	            
                //Array splitting the string by each whitespace to distinguish parts
	            String[] splitString = scanner.nextLine().split(" ");
	            
                //Splits time string from instruction by each colon
	            String[] time = splitString[0].split(":");
  
	            //Calculated the total time into ms
	            int convertedTime = Integer.parseInt(time[0])*3600000 + Integer.parseInt(time[1])*60000 + 
	            		Integer.parseInt(time[2])*1000 + Integer.parseInt(time[3]);
	            
                //Stores each part of the line read into the array to store
	            storedInstructions[0] = convertedTime;                          //time in ms
	            storedInstructions[1] = Integer.parseInt(splitString[1]);       //source floor
	            storedInstructions[2] = splitString[2].equals("Up") ? 1 : 0;    //direction of elevator (up = 1, down = 0)
	            storedInstructions[3] = Integer.parseInt(splitString[3]);       //destination floor
	            
                //adds the array of instructions into the arraylist
	            parsedInput.add(storedInstructions);
	           
	        }

            //closes scanner to stop any possible memory leak
			scanner.close();
	        
		} catch (FileNotFoundException e) {
            
            //Prints if there is no file found
			System.out.println("File not found.");
		
        }
       
        //returned arraylist
        return parsedInput;
    }

    /**
     * On thread start, this method reads in the information from the selected input file.
     * It then sends it to the scheduler using a sychronized put method.
     * If there is an instruction within the scheduler, it receives it and prints out 
     * the values from the instruction.
     */
    @Override
    public void run(){
    	
        //ArrayList containing the instructions from the file
        ArrayList<int[]> readInputs = readInput(new File("src\\inputFile.txt"));

        //for loop that goes through each array in the arraylist. this simulates each line of instructions
        for(int[] i : readInputs)
        {

            //puts the instructions into a shared memory
            scheduler.putInstruction(i);
            
            //tells the user that the instructions have been sent
            System.out.println("Instructions Sent.");
            
            //gets the instructions back from the scheduler
            int[] returnedInstruction = scheduler.getInstruction();
            
            //converts the direction from an int to a proper readable string
            String direction = returnedInstruction[2] == 1 ? "Up" : "Down";
            
            //prints out the information from the command
            System.out.println("Time: " + returnedInstruction[0] + ", Source Floor: " + returnedInstruction[1] + 
            		", Direction: " + direction + ", Destination Floor: " + returnedInstruction[3]);
            
            //slows down the thread execution
            try{
                Thread.sleep(1000);
            } catch (InterruptedException e){
                
            }
        }
    }
}
