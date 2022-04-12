package Tests;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

import Subsystems.Elevator;
import Subsystems.ElevatorController;

/**
 * Elevator Controller Test Case.
 *
 * @author Group2
 * @version April 12, 2022
 */
public class ElevatorControllerTest {

	@Test
	public void testGetPort() {
		ElevatorController ec = new ElevatorController(9010);
		assertEquals(ec.getPort(), 9010);
	}
	
	@Test
	public void testGetInfo() {
		ElevatorController ec = new ElevatorController(9010);
		int[] req = ec.getInfo();
		assertEquals(req[0], 1);
		assertEquals(req[1], 1);
		assertEquals(req[2], 1);
		assertEquals(req[3], 0);
	}
	
	@Test
	public void testFloorToVisit() {
		ElevatorController ec = new ElevatorController(9010);
		int[] req = ec.getInfo();
		ec.addRequest(req);
		int[] firstReq = ec.getFloorsToVisit(0);
		int[] secReq = ec.getFloorsToVisit(1);
		assertEquals(req[1],firstReq[0]);
		assertEquals(req[3],secReq[0]);
	}
	
	@Test
	public void testInUse() {
		ElevatorController ec = new ElevatorController(9010);
		int[] req = ec.getInfo();
		ec.addRequest(req);
		ec.setInUse();
		assertTrue(ec.getInUse());
	}
	
	@Test
	public void testHardFault() {
		ElevatorController ec = new ElevatorController(9011);
		Thread ect = new Thread(ec);
		Elevator e = new Elevator(9011);
		int[] req = {0, 2, 1, 10, 2};
		ec.addRequest(req);
		ect.start();
		while(e.getWorking()) 
		{
			e.receiveControl();
		}
		assertEquals(2,ec.getFault());
	}

}
