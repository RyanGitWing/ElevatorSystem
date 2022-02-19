import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
/**
 * Floor Test Case.
 *
 * @author Ryan Nguyen
 * @version February 19th, 2022
 */
class FloorTest 
{
	
	@Test
	void TestReadInput() 
	{
		
		// Initialize scheduler
		Scheduler scheduler = new Scheduler();	
		
		// Initialize scheduler
		Floor floor = new Floor(scheduler);
		
		// An arraylist with the expected arrays
		ArrayList<int[]> expectedInputs = new ArrayList<int[]>();
		
		// Arrays with the expected request
		int[] arr1 = {50415000, 2, 1, 4};
		expectedInputs.add(arr1);
		
		int[] arr2 = {51310005, 3, 0, 1};
		expectedInputs.add(arr2);
		
		int[] arr3 = {52388001, 1, 1, 7};
		expectedInputs.add(arr3);	
		
		int[] arr4 = {53756008, 7, 0, 1};
		expectedInputs.add(arr4);	
		
		// ArrayList containing the instructions from the file
		ArrayList<int[]> readInputs = floor.readInput(new File("src//inputFile.txt"));
		
		// Check if the values from the input file match the expected result
		for(int i = 0; i < 4; i++)
        {
			assertEquals(readInputs.get(i)[0], expectedInputs.get(i)[0]);
			assertEquals(readInputs.get(i)[1], expectedInputs.get(i)[1]);
			assertEquals(readInputs.get(i)[2], expectedInputs.get(i)[2]);
			assertEquals(readInputs.get(i)[3], expectedInputs.get(i)[3]);
        }
          
	}
	
}
