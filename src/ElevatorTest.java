import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
/**
 * Elevator Test Case.
 *
 * @author Ryan Nguyen
 * @version February 19th, 2022
 */
class ElevatorTest 
{
	
	// Initialize scheduler
	Scheduler scheduler = new Scheduler();	
	
	// Initialize elevator
	Elevator elevator = new Elevator(scheduler, 1);
				
	@Test
	void testArriveAtFloor() 
	{
		elevator.arriveAtFloor();
		assertFalse(elevator.getMotor().getOn());  
	}
	
	@Test
	void testGetElevatorID() 
	{
		assertEquals(elevator.getElevatorID(), 1);
	}
	
	@Test
	void testGetDirection() 
	{
		elevator.setDirection(0);
		assertEquals(elevator.getDirection(), 0); 
	}

}
