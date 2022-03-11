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
    
	private final static int PORTOFFSET = 5000;
    private ArrayList<ElevatorController> controllers;
    private FloorRequestHandler requestHandler;
    
    //elevatorInfo = [Port, current floor, direction, dest floor, # passengers]
    private ArrayList<int[]> elevatorInfoList;
    
    /**
     * Constructor of a Scheduler object.
     */
    public Scheduler(int requestPort, int numElevators) {
        controllers = new ArrayList<ElevatorController>();
        elevatorInfoList = new ArrayList<int[]>();
        requestHandler = new FloorRequestHandler(requestPort);
        
        Thread requestHandlerThread = new Thread(requestHandler);
        requestHandlerThread.start();
        
        for (int i = 0; i < numElevators; i++) {
            createController(PORTOFFSET+i);
        }
    }

    private void createController(int port) {
        ElevatorController controller = new ElevatorController(port);
        controllers.add(controller);
        int[] elevatorInfo = {port, 1, 1, 1, 0};
        elevatorInfoList.add(elevatorInfo);
        Thread controllerthread = new Thread(controller);
        controllerthread.start();        
    }
    
    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler(4999, 4);
        Thread schedulerThread = new Thread(scheduler);
        schedulerThread.start();
    }


    public int getElevator(int destination) {
        int closestElevator = 1000;
        int port = 0;
        for (int[] elevatorInfo : elevatorInfoList) {
            int difference = elevatorInfo[1] - destination;
            boolean isEligible = (difference >= 0 && elevatorInfo[2] == 0) || (difference <= 0 && elevatorInfo[2] == 1);
            //for direction could include 2 for not moving
            if (isEligible && Math.abs(difference) < closestElevator) {
                port = elevatorInfo[0];
            }
        }
        return port;
    }
    
    
    @Override
    public void run() {
        while (true) {
            int[] request = requestHandler.getRequest();
            int elevatorPort = getElevator(request[1]);
            controllers.get(elevatorPort - PORTOFFSET).addRequest(request);
        }
    }
}