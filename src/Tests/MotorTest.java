package Tests;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

import Subsystems.Motor;

/**
 * Motor Test Case.
 *
 * @author Group2
 * @version March 27, 2022
 */
public class MotorTest {

	@Test
	public void testToggleMotor() {
		Motor motor = new Motor();
		motor.toggleMotor(true);
		assertTrue(motor.getOn());
	}

}
