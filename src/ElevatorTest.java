import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

/**
 * Elevator Test Case.
 *
 * @author Ryan Nguyen
 * @version March 12, 2022
 */
public class ElevatorTest {

	@Test
	public void testSetDirection() 
	{
		Elevator elevator = new Elevator(6006);

		elevator.setDirection(0);
		assertEquals(elevator.getDirection(), 0); 
	}
	
	@Test
	public void testDecodeControl() 
	{
		Elevator elevator = new Elevator(8888);
		byte []b = {2};
		elevator.decodeControl(b);
		assertEquals(elevator.getDirection(), 1); 
	}
	
	@Test
	public void testGetElevatorID() 
	{
		Elevator elevator = new Elevator(7777);
		assertEquals(elevator.getElevatorID(), 7777); 
	}

}
