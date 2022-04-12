package Tests;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;
import Subsystems.Elevator;

/**
 * Elevator Test Case.
 *
 * @author Group2
 * @version April 12, 2022
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
	public void testDoorRepairToCloseFault() 
	{
		Elevator elevator = new Elevator(9999);
		byte []b = {21};
		elevator.decodeControl(b);
		assertTrue(elevator.getDoor()); 
	}
	
	@Test
	public void testDoorRepairToOpenFault() 
	{
		Elevator elevator = new Elevator(9009);
		byte []b = {22};
		elevator.decodeControl(b);
		assertFalse(elevator.getDoor()); 
	}
	
	@Test
	public void testGetElevatorID() 
	{
		Elevator elevator = new Elevator(7777);
		assertEquals(elevator.getElevatorID(), 7777); 
		
	}
}
