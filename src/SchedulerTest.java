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

	// Initialize scheduler
	Scheduler scheduler = new Scheduler();	
		
	Elevator elevator = new Elevator(scheduler, 1);
				
	// Initialize scheduler
	Floor floor = new Floor(scheduler);

	@Test
	void TestMoveElevator() {
		
		scheduler.addElevator(elevator);

		// Assign a variable an instruction
		int[] request = {1400150, 2, 1, 4};
		
		// Put the instructions into a shared memory
        scheduler.putRequest(request);
        
        scheduler.moveElevator(elevator, request[1], request[2]);
        
        assertEquals(elevator.getCurrentFloor(), request[1]);
          
	}
	
}
