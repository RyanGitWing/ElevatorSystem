import java.util.ArrayList;
/**
 * The Scheduler class takes requests from the floor subsystem
 * and executes each request. Executing the request entails commanding
 * the elevator to move to the floor where the request was made and then
 * commanding the elevator to move to the passenger's destination floor.
 * 
 * @author Aleksandar Veselinovic, Dominique Giguere Samson
 * @version March 12, 2022
 *
 */

public class Scheduler implements Runnable {
    
    //Offset for any port made 
	private final static int PORTOFFSET = 5000;
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
        controllers = new ArrayList<ElevatorController>();
        for (int i = 0; i < numElevators; i++) {
            createController(PORTOFFSET+i);
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
     * Create a elevator controller with the defined port
     * 
     * @param port integer that holds the port
     */
    private void createController(int port) {
        ElevatorController controller = new ElevatorController(port);
        controllers.add(controller);
        Thread controllerthread = new Thread(controller);
        controllerthread.start();        
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
            int difference = elevatorInfo[1] - destination;
            if (!controller.getInUse()) {
                isEligible = ((difference >= 0 && elevatorInfo[2] == 0) || (difference <= 0 && elevatorInfo[2] == 1));
            }
            //for direction could include 2 for not moving
            if (isEligible && Math.abs(difference) < closestElevator) {
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
        Scheduler scheduler = new Scheduler(1);
        Thread schedulerThread = new Thread(scheduler);
        FloorRequestHandler requestHandler = new FloorRequestHandler(4999);
        scheduler.addHandler(requestHandler);
        Thread requestHandlerThread = new Thread(requestHandler);
        schedulerThread.start();
        requestHandlerThread.start();
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
            System.out.println("Scheduler using Elevator " + elevatorPort + " to execute request " + request[1] + "," + request[3]);
            controllers.get(elevatorPort - PORTOFFSET).executeRequest(request);
        }
    }
}
