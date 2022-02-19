import java.util.ArrayList;
/**
 * The Scheduler class takes requests from the floor subsystem
 * and executes each request. Executing the request entails commanding
 * the elevator to move to the floor where the request was made and then
 * commanding the elevator to move to the passenger's destination floor.
 * 
 * @author Aleksandar Veselinovic
 * @version February 19, 2022
 *
 */
public class Scheduler implements Runnable {
	
	private ArrayList<int[]> requests;
	private ArrayList<Elevator> elevators;//currently only 1 elevator
	
	/**
	 * Constructor of a Scheduler object.
	 */
	public Scheduler() {
		requests = new ArrayList<int[]>();
		elevators = new ArrayList<Elevator>();
	}
	
	/**
	 * This method adds an elevator to the list of elevators
	 * that the scheduler controls.
	 *
	 * @param elevator the elevator to add to the list of controlled elevators
	 */
	public void addElevator(Elevator elevator) {
		elevators.add(elevator);
	}
	
	/**
	 * This method adds a request to the list of requests
	 * that the scheduler will execute. When the request
	 * is added, any waiting threads will be notified.
	 *
	 * @param request the request to add to the request list
	 */
	public synchronized void putRequest(int[] request) {
		System.out.println("Received request!");
		requests.add(request);
		notifyAll();
	}

	/**
	 * This method stops an elevator if it is currently at the floor
	 * it needs to stop at.
	 *
	 * @param currentFloor the floor the elevator is currently at
	 * @param destination the floor the elevator needs to stop at
	 */
	public synchronized void stopAtFloor(int currentFloor, int destination) {
		int[] currentRequest = requests.get(0);
		if (currentFloor == destination) {
			stopElevator(elevators.get(0));
		}
	}

	/**
	 * This method moves an elevator to the specified floor.
	 *
	 * @param elevator the elevator to move
	 * @param destination the floor the elevator needs to stop at
	 * @param direction the direction the elevator needs to go
	 */
	public void moveElevator(Elevator elevator, int destination, int direction) {
		
		elevator.setDoor(true);
		
		System.out.println("Elevator door closed");
		
		elevator.setDestination(destination);		
		elevator.setDirection(direction);
		
		String directionString = direction == 1 ? "up" : "down";
		
		System.out.println("Elevator going " + directionString + " to floor " + destination + "\n");
		elevator.turnOnMotor();
		
	}

	/**
	 * This method commands an elevator to stop moving.
	 *
	 * @param elevator the elevator to stop
	 */
	public void stopElevator(Elevator elevator) {
		elevator.arriveAtFloor();
		
		System.out.println("\n" + "Elevator has arrived at floor " + elevator.getCurrentFloor());
		
		elevator.setDoor(false);
		
		System.out.println("Elevator door opened \n");
		
	} 
	
	/**
	 * This method reads and executes the first request in the request list.
	 * It will wait until there is a request to read and execute.
	 */
	public synchronized void readRequest() {
		if (requests.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {

			}
		} else {
			
			System.out.println("Scheduler executing request \n");
			
			int currentFloor = elevators.get(0).getCurrentFloor();
			int sourceFloor = requests.get(0)[1];
			
			if (sourceFloor != currentFloor) {//if the elevator is not already at the floor it needs to go to
				
				int initialDirection = 0;//the direction the elevator will need to move is set to a default of down
				
				if ((sourceFloor - currentFloor) > 0) {//if the next floor to visit is above the elevator's current floor
					initialDirection = 1;//set the direction the elevator will need to move to up
				}
							
				moveElevator(elevators.get(0), sourceFloor, initialDirection);//move the elevator to the floor where the request was made
				
			} else {//if the elevator is already at the floor it needs to go to
				System.out.println("Elevator is already at floor " + sourceFloor);
			}
			
			moveElevator(elevators.get(0), requests.get(0)[3], requests.get(0)[2]);//move the elevator to the passenger's destination floor
			
			requests.remove(0);
		}
	}

	@Override
	public void run() {
		while (true) {
			readRequest();
		}
	}

}
