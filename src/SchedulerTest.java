import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
/**
 * Scheduler Test Case.
 *
 * @author Ryan Nguyen
 * @version February 19th, 2022
 */
class SchedulerTest 
{
	
	@Test
	void TestMoveElevator() 
	{
		// Initialize scheduler
		Scheduler scheduler = new Scheduler();	
		
		// Initialize elevator
		Elevator elevator = new Elevator(scheduler, 1);
		
		// Add elevator to scheduler
		scheduler.addElevator(elevator);

		// Assign a request variable
		int[] request = {1400150, 2, 1, 4};
		
		// Put the request into a shared memory
        scheduler.putRequest(request);
        
        // Move the elevator to the source floor (2) in the up direction (1)
        scheduler.moveElevator(elevator, request[1], request[2]);
        
        // Check if the floor that the elevator is at matches the one requested
        assertEquals(elevator.getCurrentFloor(), request[1]);
          
	}
	
}
