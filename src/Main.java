/**
 * Main class that run the sandwich making 
 * chef program.
 * 
 * @author Ryan Nguyen
 * @version January 29, 2022
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

		Thread elevator = new Thread(new Elevator(scheduler));
		Thread floor = new Thread(new Floor(scheduler));
		Thread schedulerThread = new Thread(scheduler);
		
		elevator.start();
		floor.start();
		schedulerThread.start();
		
	}

}
