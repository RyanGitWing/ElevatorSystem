/**
 * Main class that run the elevator system program.
 * 
 * @author Ryan Nguyen
 * @version February 19, 2022
 *
 */
public class Main 
{
	/**
	 * Main function that runs the program.
	 * 
	 * @param args
	 */
	public static void main(String[] args) 
	{
		Scheduler scheduler = new Scheduler();
		Elevator elevator = new Elevator(scheduler, 1);
		Floor floor = new Floor(scheduler);
		
		scheduler.addElevator(elevator);

		Thread elevatorThread = new Thread(elevator);
		Thread floorThread = new Thread(floor);
		Thread schedulerThread = new Thread(scheduler);
		
		elevatorThread.start();
		floorThread.start();
		schedulerThread.start();
		
	}

}
