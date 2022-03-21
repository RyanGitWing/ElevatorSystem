import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
/**
 * Elevator Controller Test Case.
 *
 * @author Ryan Nguyen
 * @version March 12, 2022
 */
class ElevatorControllerTest {
	
	@Test
	void testGetPort() {
		ElevatorController ec = new ElevatorController(9010);
		assertEquals(ec.getPort(), 9010);
	}
	
	@Test
	void testGetInfo() {
		ElevatorController ec = new ElevatorController(9010);
		int[] req = ec.getInfo();
		assertEquals(req[0], 1);
		assertEquals(req[1], 1);
		assertEquals(req[2], 1);
		assertEquals(req[3], 0);
	}
	
}
