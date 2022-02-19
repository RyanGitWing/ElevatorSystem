/**
 * Elevator class that creates an elevator object which moves
 * from one floor to another based on the request. 
 *
 * @author Ryan Nguyen
 * @version February 19th, 2022
 */
public class Elevator implements Runnable
{
		
	private Scheduler scheduler;
	
	private Motor motor;
	
	private static final int TIME_BETWEEN_EACH_FLOOR = 3000;

	private static final int TIME_TO_OPEN_CLOSE = 1000; 
	
	private int id, currentFloor, direction, destination;
	
	private boolean doorClosed;

	/**
	 * Elevator Constructor.
	 * 
	 * @param scheduler is the scheduler that assigns task to elevator.
	 * @param id is the elevator ID.
	 */
	public Elevator(Scheduler scheduler, int id) 
	{
		this.scheduler = scheduler;
		this.id = id;
		currentFloor = 1;
		direction = 1;
		doorClosed = false;
		motor = new Motor();
	}
	
	/**
	 * Turns on the elevator motor.
	 * Elevator will move between floors until it reaches
	 * the desired destination floor.
	 */
	public void turnOnMotor() 
	{
		// Turn on elevator motor
		motor.toggleMotor(true);
		
		// Will keep moving until reaches destination floor
		while (motor.getOn()) 
		{
			int temp = currentFloor - destination;
			int distanceBetweenFloor = Math.abs(temp);
			
			// Sleep the elevator thread for the time it takes to get to destination
			try {
				Thread.sleep(TIME_BETWEEN_EACH_FLOOR * distanceBetweenFloor);
			} catch (InterruptedException e){
				
			}
			
			// Decrement the floor to simulate going down a floor level,
			// if the current floor is above the destination floor
			if (temp > 0) 
			{
				currentFloor--;
				
			// else, do the opposite
			} else {
				currentFloor++;
			}
			
			// Display the elevator traversing each floor to get to destination floor
			System.out.println("Floor " + currentFloor + " detected Elevator");
			
			// Call stopAtFloor when reach destination
			scheduler.stopAtFloor(currentFloor, destination);
		}

	}
	
	/**
	 * Once arrived at destination floor, turn the 
	 * elevator motor off.
	 */
	public void arriveAtFloor() 
	{
		motor.toggleMotor(false);
	}
	
	/**
	 * Getter function for elevator motor.
	 * 
	 * @return The elevator motor.
	 */
	public Motor getMotor() 
	{
		return motor;
	}
	
	/**
	 * Open/Close the door based on request.
	 * 
	 * @param closeDoor closes/opens the elevator door.
	 */
	public void setDoor(boolean closeDoor) 
	{
		
		// Sleep the elevator thread for the amount
		// of time it takes to open/close door
		try {
			Thread.sleep(TIME_TO_OPEN_CLOSE);
		} catch (InterruptedException e) {
			
		}
		
		doorClosed = closeDoor;
		
	}
	
	/**
	 * Get the elevator ID.
	 * 
	 * @return The elevator ID.
	 */
	public int getElevatorID() 
	{	
		return this.id;
	}
	
	/**
	 * Get the current floor that the elevator is at.
	 * 
	 * @return The current floor of the elevator.
	 */
	public int getCurrentFloor() 
	{
		return currentFloor;
	}
	
	/**
	 * Set the destination of the elevator.
	 * 
	 * @param floor is the destination floor.
	 */
	public void setDestination(int floor) 
	{
		destination = floor;
	}
	
	/**
	 * Get the direction the elevator is going.
	 * 
	 * @return The direction of the elevator either up (1) or down (0).
	 */
	public int getDirection() 
	{
		return direction;
	}

	/**
	 * Set the direction of the elevator.
	 * 
	 * @param direction is the direction the elevator will go.
	 */
	public void setDirection(int direction)
	{
		this.direction = direction;
	}
	
	@Override
	/**
	 * Runnable that runs the elevator thread.
	 */
	public void run() 
	{	
		while (true) 
		{	
			// Slow down the thread execution
			try{
				Thread.sleep(1000);
			} catch (InterruptedException e){
					
			}
		}
	}

}
