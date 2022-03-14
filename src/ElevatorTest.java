import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
/**
 * Elevator Test Case.
 *
 * @author Ryan Nguyen
 * @version March 12, 2022
 */
class ElevatorTest 
{
	
	@Test
	void testSetDirection() 
	{
		Elevator elevator = new Elevator(6006);

		elevator.setDirection(0);
		assertEquals(elevator.getDirection(), 0); 
	}
	
	@Test
	void testDecodeControl() 
	{
		Elevator elevator = new Elevator(8888);
		byte []b = {2};
		elevator.decodeControl(b);
		assertEquals(elevator.getDirection(), 1); 
	}
	
	@Test
	void testGetElevatorID() 
	{
		Elevator elevator = new Elevator(7777);
		assertEquals(elevator.getElevatorID(), 7777); 
	}

}
