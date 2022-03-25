import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

/**
 * Elevator Controller Test Case.
 *
 * @author Ryan Nguyen
 * @version March 12, 2022
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

}
