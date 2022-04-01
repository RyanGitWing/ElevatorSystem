package Subsystems;
import java.util.ArrayList;
/**
 * The Scheduler class takes requests from the floor subsystem
 * and executes each request. Executing the request entails commanding
 * the elevator to move to the floor where the request was made and then
 * commanding the elevator to move to the passenger's destination floor.
 * 
 * @author Group2
 * @version March 27, 2022
 *
 */

public class Scheduler implements Runnable {
    
    //Offset for any port made 
	private final static int PORTOFFSET = 5000;
	//Number of Elevators (must be consistent between Scheduler.java and Elevator.java
	private final static int NUMELEVATORS = 2;
    //List of elevator controller objects
    private ArrayList<ElevatorController> controllers;
    //Floor Request Handler object
    private FloorRequestHandler requestHandler;
    
    //elevatorInfo = [Port, current floor, direction, dest floor, # passengers]
    
    /**
     * Constructor of a Scheduler object.
     * 
     * @param requestPort 
     * @param numElevators number of elevators used in the system
     */
    public Scheduler(int numElevators) {
    	requestHandler = new FloorRequestHandler(4999);
        controllers = new ArrayList<ElevatorController>();
        for (int i = 0; i < numElevators; i++) {
            //createController(PORTOFFSET+i);
        	ElevatorController controller = new ElevatorController(PORTOFFSET + i);
            controllers.add(controller);
        }
    }
    
    /**
     * Adds floor request handler object to this object
     * 
     * @param frh floorrequesthandler object
     */
    public void addHandler(FloorRequestHandler frh) {
    	requestHandler = frh;
    }
    
    /**
     * Starts floor request handler threads
     */
    public void startHandler() {
    	Thread requestHandlerThread = new Thread(requestHandler);
    	requestHandlerThread.start();
    }

    /**
     * Starts controller threads
     */
    public void startControllers() {
    	for (int i = 0; i < controllers.size(); i++) {
    		Thread controllerthread = new Thread(controllers.get(i));
            controllerthread.start();
    	}
    }


    /**
     * Determines which elevator is best used to handle the request.
     * This is done by determining which is closest to the request which
     * returns which port is available.
     * 
     * 
     * @param destination destination floor
     * @return elevator which will be used
     */
    public int getElevator(int destination) {
        int closestElevator = 1000;
        int port = PORTOFFSET;
        boolean isEligible = false;
        for (ElevatorController controller : controllers) {
        	int[] elevatorInfo = controller.getInfo();
            int difference = elevatorInfo[0] - destination;
            if (!controller.getInUse()) {
                isEligible = ((difference >= 0 && elevatorInfo[1] == 0) || (difference <= 0 && elevatorInfo[1] == 1));
            }
            //for direction could include 2 for not moving
            if (isEligible && Math.abs(difference) < closestElevator) {
            	closestElevator = Math.abs(difference);
                port = controller.getPort();
            }
        }
        return port;
    }
    
    /**
     * Main method to run the class. Creates a scheduler thread
     * and a request handler thread.
     * @param args
     */
    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler(NUMELEVATORS);
        Thread schedulerThread = new Thread(scheduler);
        schedulerThread.start();
        scheduler.startHandler();
        scheduler.startControllers();
    }
    
    /**
     * When the thread is started, this gets the request that will be used.
     * Sends the request to the controller to be executed.
     */
    @Override
    public void run() {
        while (true) {
            int[] request = requestHandler.getRequest();
            
            int elevatorPort = getElevator(request[1]);
            System.out.println(TimeConverter.msToTime(request[0]) + ": Scheduler using Elevator " + (elevatorPort - 4999) + " to execute request " + request[1] + "," + request[3]);
            //instead of execute request, simply addRequest to elevatorcontroller thread
            //controllers.get(elevatorPort - PORTOFFSET).executeRequest(request);
            controllers.get(elevatorPort - PORTOFFSET).addRequest(request);
        }
    }
    
    /**
     * CURRENTLY UNUSED - MAY BE NEEDED FOR FUTURE REFERENCE
     * Create a elevator controller with the defined port
     * 
     * @param port integer that holds the port
     
    private void createController(int port) {
        ElevatorController controller = new ElevatorController(port);
        controllers.add(controller);
        Thread controllerthread = new Thread(controller);
        controllerthread.start();     
    }*/
}
