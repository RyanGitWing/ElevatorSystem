import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The floor class is used to simulate the arrival of passengers
 * to the elevators and simulating buttons being pressed.
 *
 * @author tleung
 */
public class Floor implements Runnable{
    
    private Scheduler scheduler;

    public Floor(Scheduler scheduler){
    	
        this.scheduler = scheduler;
        
    }

    public ArrayList<int[]> readInput(File inputFile){
    	
        ArrayList<int[]> parsedInput = new ArrayList<int[]>();
        
		try {

	        Scanner scanner = new Scanner(inputFile);
	        	        	        
	        
	        while(scanner.hasNextLine())
	        {
	        	
	            int[] storedInstructions = new int[4];
	            
	            String[] splitString = scanner.nextLine().split(" ");
	            	            
	            String[] time = splitString[0].split(":");
  
	            
	            int convertedTime = Integer.parseInt(time[0])*3600000 + Integer.parseInt(time[1])*60000 + 
	            		Integer.parseInt(time[2])*1000 + Integer.parseInt(time[3]);
	            
	            storedInstructions[0] = convertedTime;
	            storedInstructions[1] = Integer.parseInt(splitString[1]);
	            storedInstructions[2] = splitString[2].equals("Up") ? 1 : 0;
	            storedInstructions[3] = Integer.parseInt(splitString[3]);
	            
	            parsedInput.add(storedInstructions);
	           
	        }
			scanner.close();
	        
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
		}
       
        return parsedInput;
    }

    @Override
    public void run(){
    	
        ArrayList<int[]> readInputs = readInput(new File("D:\\School\\SYSC 3303\\workspace\\ElevatorProject\\src\\inputFile.txt"));

        for(int[] i : readInputs)
        {
            scheduler.putInstruction(i);
            
            System.out.println("Instructions Sent.");
            
            int[] returnedInstruction = scheduler.getInstruction();
            
            String direction = returnedInstruction[2] == 1 ? "Up" : "Down";
            
            System.out.println("Time: " + returnedInstruction[0] + ", Source Floor: " + returnedInstruction[1] + 
            		", Direction: " + direction + ", Destination Floor: " + returnedInstruction[3]);
            
            try{
                Thread.sleep(1000);
            } catch (InterruptedException e){
                
            }
        }
    }
}
