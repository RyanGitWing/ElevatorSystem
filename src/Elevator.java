import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Elevator class that creates an elevator object which moves
 * from one floor to another based on the request. 
 *
 * @author Ryan Nguyen
 * @version February 5th, 2022
 */
public class Elevator implements Runnable{
		
	private Scheduler scheduler;
	
	/**
	 * Elevator Constructor.
	 * 
	 * @param scheduler
	 * @param id
	 */
	public Elevator(Scheduler scheduler) {
		
		this.scheduler = scheduler;
		
	}
	
	@Override
	public void run() {
		
		while (true) 
		{
			
			int[] returnedInstruction = scheduler.getInstruction();
            
            String direction = returnedInstruction[2] == 1 ? "Up" : "Down";
            
            System.out.println("Time: " + returnedInstruction[0] + ", Source Floor: " + returnedInstruction[1] + 
            		", Direction: " + direction + ", Destination Floor: " + returnedInstruction[3]);
			
			scheduler.putInstruction(returnedInstruction);
			
			System.out.println("Instructions Sent Back.");
			
			
		}
		
	}
	
	

}
