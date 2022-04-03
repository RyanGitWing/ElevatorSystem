package Subsystems;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
/**
 * The Scheduler class takes requests from the floor subsystem
 * and executes each request. Executing the request entails commanding
 * the elevator to move to the floor where the request was made and then
 * commanding the elevator to move to the passenger's destination floor.
 * 
 * @author Group2
 * @version April 12, 2022
 */

public class Scheduler implements Runnable {
    
    //Offset for any port made 
	private final static int PORTOFFSET = 5000;
	//Number of Elevators (must be consistent between Scheduler.java and Elevator.java
	private final static int NUMELEVATORS = 4;
    //List of elevator controller objects
    private ArrayList<ElevatorController> controllers;
    //Floor Request Handler object
    private FloorRequestHandler requestHandler;
    //Elevator stats required for GUI [elevatorPort, current floor, direction, moving, fault]
    private ArrayList<int[]> elevatorDisplayStats;
    //Gui drawer
    private GUI gui;
    private int defaultPort;
    
    private LocalTime time = LocalTime.now();
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SS");
    private String fTime  = time.format(formatter);
    
    /**
     * Constructor of a Scheduler object.
     * 
     * @param requestPort 
     * @param numElevators number of elevators used in the system
     */
    public Scheduler(int numElevators) {
    	requestHandler = new FloorRequestHandler(4999);
        controllers = new ArrayList<ElevatorController>();
        elevatorDisplayStats = new ArrayList<int[]>();
        defaultPort = PORTOFFSET;
        gui = new GUI();
        for (int i = 0; i < numElevators; i++) {
        	ElevatorController controller = new ElevatorController(PORTOFFSET + i);
            controllers.add(controller);
            int[] elevatorStats = {PORTOFFSET + i, 1, 1, 0, 0};
            elevatorDisplayStats.add(elevatorStats);
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
        int port = defaultPort;
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
        defaultPort++;
        if (defaultPort == 5004) defaultPort = 5000;
        return port;
    }
    
    /**
     * Collects information about each elevator, to use for the GUI
     * 
     */
    public void updateElevatorDisplayStats() {
    	for (ElevatorController controller : controllers) {
    		int[] info = controller.getInfo();
    		int port = controller.getPort();
    		int fault = controller.getFault();
    		int moving = controller.getMoving() ? 1 : 0; 
	    	for (int[] elevator : elevatorDisplayStats) {
	    		if (port == elevator[0]) {//Find the right entry by matching Elevator Port
	    			elevator[1] = info[0]; //update Current floor
	    			elevator[2] = info[1]; //update direction
	    			elevator[3] = moving; //update moving
	    			elevator[4] = fault; //update fault
	    		}
	    	}
    	}
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
            if (request[0] != -1) {//Check for non empty request list
                int elevatorPort = getElevator(request[1]);
                System.out.println(fTime + " (Scheduler)" + ": Scheduler using Elevator " + (elevatorPort - 4999) + " to execute request " + request[1] + "," + request[3]);
                System.out.println(fTime + " (Scheduler)" + " Floor " + request[3] + " lamp turned on.");
                controllers.get(elevatorPort - PORTOFFSET).addRequest(request);
            }
            updateElevatorDisplayStats();
            gui.updateGUI(elevatorDisplayStats);
        }
    }
}
