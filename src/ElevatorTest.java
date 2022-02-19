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
class ElevatorTest {

	// Initialize scheduler
	Scheduler scheduler = new Scheduler();	
		
	Elevator elevator = new Elevator(scheduler, 1);
				
	@Test
	void TestTurnOnMotor() {
		
		elevator.arriveAtFloor();
		
		assertFalse(elevator.getMotor().getOn());
          
	}
	
	@Test
	void TestGetElevatorID() {
		
		assertEquals(elevator.getElevatorID(), 1);
          
	}
	
	@Test
	void TestGetDirection() {
		
		elevator.setDirection(0);
		
		assertEquals(elevator.getDirection(), 0);
          
	}
	
}
