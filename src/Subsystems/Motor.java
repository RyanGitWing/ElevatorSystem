package Subsystems;
/**
 * Motor class that creates an motor object which
 * turns the elevator motor on and off.
 *
 * @author Group2
 * @version April 12, 2022
 */
public class Motor 
{ 
	private boolean isOn; 
	
	/**
	 * Motor Constructor.
	 */
	public Motor() 
	{
		// By default the motor is off
		isOn = false;
	}
	
	/**
	 * Setter motor function. Changes the state
	 * of the motor.
	 * 
	 * @param isOn turns the motor on or off.
	 */
	public void toggleMotor(boolean isOn) 
	{
		this.isOn = isOn;
	}
	
	/**
	 * Getter method for the motor state.
	 * 
	 * @return The state of the motor.
	 */
	public boolean getOn() 
	{
		return isOn;
	}
}
