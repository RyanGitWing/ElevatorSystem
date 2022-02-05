import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

/**
 * Scheduler Test Case.
 *
 * @author Ryan Nguyen
 * @version February 5th, 2022
 */
class SchedulerTest {

	@Test
	void TestScheduler() {
		
		// Initialize scheduler
		Scheduler scheduler = new Scheduler();	
		
		// Initialize scheduler
		Floor floor = new Floor(scheduler);
		
        // ArrayList containing the instructions from the file
        ArrayList<int[]> readInputs = floor.readInput(new File("src\\inputFile.txt"));
        
        // Loop through each line and get instructions
    	for (int i = 0; i < readInputs.size(); i++) 
    	{
    		// Assign a variable an instruction
    		int[] instruction = readInputs.get(i);
    		
    		// Put the instructions into a shared memory
            scheduler.putInstruction(instruction);
            
            // Have a temporary variable get the instructions stored
            int[] temp = scheduler.getInstruction();
            
            // Loop through each element to see if it matches the elements from instruction variable
    		for(int j = 0; j < temp.length; j++) 
    		{
    			assertEquals(instruction[j], temp[j]);
    			System.out.println(instruction[j]);
    		}
    		
    	}
          
	}
	
}
