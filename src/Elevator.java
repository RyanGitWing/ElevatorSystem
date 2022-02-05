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
	 */
	public Elevator(Scheduler scheduler) {
		
		this.scheduler = scheduler;
		
	}
	
	@Override
	/**
	 * Runnable that fetches the instructions from scheduler
	 * and prints the instruction if it was received.
	 * Also, it sends the instruction back to the scheduler.
	 */
	public void run() {
		
		while (true) 
		{
			// Get the instructions from scheduler
			int[] returnedInstruction = scheduler.getInstruction();
		    
			// Convert the direction from an integer to a proper readable string
			String direction = returnedInstruction[2] == 1 ? "Up" : "Down";
		    
			// Output the information from the command
			System.out.println("Time: " + returnedInstruction[0] + ", Source Floor: " + returnedInstruction[1] + 
					", Direction: " + direction + ", Destination Floor: " + returnedInstruction[3]);
			
			// Return the instructions to scheduler
			scheduler.putInstruction(returnedInstruction);
			
			// Let user know the instruction was sent back
			System.out.println("Instructions Sent Back.");
			
			// Slow down the thread execution
			try{
				Thread.sleep(1000);
			} catch (InterruptedException e){
					
			}
		}
	}
	
	

}
