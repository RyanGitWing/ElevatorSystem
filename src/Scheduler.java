import java.util.ArrayList;
/**
 * Main class that run the elevator system program.
 * 
 * @author Ryan Nguyen
 * @version February 19, 2022
 *
 */
public class Scheduler implements Runnable {
	
	private ArrayList<int[]> requests;
	private ArrayList<Elevator> elevators;
	
	public Scheduler() {
		requests = new ArrayList<int[]>();
		elevators = new ArrayList<Elevator>();
	}
	
	public void addElevator(Elevator elevator) {
		elevators.add(elevator);
	}
	
	public synchronized void putRequest(int[] request) {
		System.out.println("Received request!");
		requests.add(request);
		notifyAll();
	}

	public synchronized void stopAtFloor(int currentFloor, int destination) {
		int[] currentRequest = requests.get(0);
		if (currentFloor == destination) {
			stopElevator(elevators.get(0));
		}
	}

	public void moveElevator(Elevator elevator, int destination, int direction) {
		
		elevator.setDoor(true);
		
		System.out.println("Elevator door closed");
		
		elevator.setDestination(destination);		
		elevator.setDirection(direction);
		
		String directionString = direction == 1 ? "up" : "down";
		
		System.out.println("Elevator going " + directionString + " to floor " + destination + "\n");
		elevator.turnOnMotor();
		
	}

	public void stopElevator(Elevator elevator) {
		elevator.arriveAtFloor();
		
		System.out.println("\n" + "Elevator has arrived at floor " + elevator.getCurrentFloor());
		
		elevator.setDoor(false);
		
		System.out.println("Elevator door opened \n");
		
	} 
	
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
			
			if (sourceFloor != currentFloor) {
				
				int initialDirection = 0;
				
				if ((sourceFloor - currentFloor) > 0) {
					initialDirection = 1;
				}
							
				moveElevator(elevators.get(0), sourceFloor, initialDirection);
				
			} else {
				System.out.println("Elevator is already at floor " + sourceFloor);
			}
			
			moveElevator(elevators.get(0), requests.get(0)[3], requests.get(0)[2]);
			
			requests.remove(0);
		}
	}

	@Override
	public void run() {
		while (true) {
			readRequest();
			// get request from floor
			// execute request
		}
	}

}