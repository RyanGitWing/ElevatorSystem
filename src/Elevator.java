/**
 * Elevator class that creates an elevator object which moves
 * from one floor to another based on the request. 
 *
 * @author Ryan Nguyen
 * @version February 5th, 2022
 */
public class Elevator implements Runnable{
		
	private Scheduler scheduler;
	
	private static final int TIME_BETWEEN_EACH_FLOOR = 3000;

	private static final long TIME_TO_OPEN_CLOSE = 1000; 
	
	private int id;
	
	private int currentFloor;
		
	private boolean doorClosed;
	
	private Motor motor;
	
	private int direction;
	
	private int destination;

	
	/**
	 * Elevator Constructor.
	 * 
	 * @param scheduler
	 */
	public Elevator(Scheduler scheduler, int id) {
		
		this.scheduler = scheduler;
		this.id = id;
		currentFloor = 1;
		direction = 1;
		doorClosed = false;
		motor = new Motor();
		
	}
	
	/**
	 * Get the elevator ID.
	 * 
	 * @return
	 */
	public int getElevatorID() {
		
		return this.id;
		
	}
	
	public Motor getMotor() {
		return motor;
	}
	
	/**
	 * Move elevator to the next floor requested.
	 */
	public void turnOnMotor() {
		
		motor.toggleMotor(true);
		
		while (motor.getOn()) 
		{
			int temp = currentFloor - destination;
			int distanceBetweenFloor = Math.abs(temp);
			
			try {
				Thread.sleep(TIME_BETWEEN_EACH_FLOOR * distanceBetweenFloor);
			} catch (InterruptedException e){
				
			}
			
			if (temp > 0) {
				currentFloor--;
			} else {
				currentFloor++;
			}
			System.out.println("Floor " + currentFloor + " detected Elevator");
			scheduler.stopAtFloor(currentFloor, destination);
		}

	}
	
	public void setDestination(int floor) {
		destination = floor;
	}
	
	/**
	 * Open/Closed the door based on request.
	 * 
	 * @param openDoor
	 */
	public void setDoor(boolean closeDoor) {
		
		try {
			Thread.sleep(TIME_TO_OPEN_CLOSE);
		} catch (InterruptedException e) {
			
		}
		
		doorClosed = closeDoor;
		
	}
	
	public int getCurrentFloor() {
		return currentFloor;
	}
	
	
	/**
	 * 
	 * 
	 */
	public void arriveAtFloor() {
	
		motor.toggleMotor(false);
		
	}
	
	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	
	@Override
	/**
	 * Runnable that fetches the instructions from scheduler
	 * and prints the instruction if it was received.
	 * Also, it sends the instruction back to the scheduler.
	 */
	public void run() {
		
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
