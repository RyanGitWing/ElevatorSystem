import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;
import org.junit.jupiter.api.TestFactory;

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

	@Test
	public void testMovingFault(){
		Elevator elevator =  new Elevator(9999);
		Motor motor =  new Motor();
		ElevatorController ec = new ElevatorController(9999);
		byte []b = {4};
		while(true){
			elevator.decodeControl(b);

			if(elevator.getError() == 0){
				System.out.println("System Failed.");
				assertFalse(motor.getOn());
				return;
			} else {
				assertTrue(elevator.getDoor());
				return;

			}
		}
	}

	@Test
	public void testDoorRepairFault(){
		Elevator elevator =  new Elevator(6356);
		byte []b = {0};

		while(true){
			elevator.decodeControl(b);

			if(elevator.getError() == 0){
				System.out.println("System Error.");
				assertFalse(elevator.getDoor());
				return;
			} else {
				assertTrue(elevator.getDoor());
				return;

			}
		}
	}

}
