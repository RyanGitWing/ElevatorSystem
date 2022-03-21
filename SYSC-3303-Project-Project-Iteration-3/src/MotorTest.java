import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
/**
 * Motor Test Case.
 *
 * @author Ryan Nguyen
 * @version March 12, 2022
 */
class MotorTest {

	@Test
	void testToggleMotor() {
		Motor motor = new Motor();
		motor.toggleMotor(true);
		assertTrue(motor.getOn());
	}

}
