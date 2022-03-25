import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

/**
 * Motor Test Case.
 *
 * @author Ryan Nguyen
 * @version March 12, 2022
 */
public class MotorTest {

	@Test
	public void testToggleMotor() {
		Motor motor = new Motor();
		motor.toggleMotor(true);
		assertTrue(motor.getOn());
	}

}
