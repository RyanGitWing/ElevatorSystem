---------------------- Package ----------------------

Communication Protocol.docx
ElevatorProject
README.txt
elevator_math.xlsx
Class Diagram
Sequence Diagram
State Diagram
Timing Diagrams
Demo_Video.mp4 (floor console not displayed b/c we want to
see the other two subsystems better)
Group2_Final_Report.pdf

---------------------- Description ----------------------

Elevator system that reads and process an instruction file.
The floor sends request through UDP to the floorRequestHandler 
then the scheduler takes the request from the floorRequestHandler.
The scheduler chooses the closest elevator to handle the request 
and sends it to an elevatorController. The elevatorController sends 
controls through UDP to manipulate its associated elevator to
handle the request.

---------------------- Responsibilities ----------------------

A lot of the work was done together in a meeting with every 
member. Everyone was providing input and aiding in the coding 
and design process. For the sake of dividing some of the work,
each person was assigned tasks.

Tyler
    - Test Classes
    - GUI creation
Aleksandar
    - Fix Fault Handling
    - Test Classes
Dominique
    - GUI creation
Ryan
    - Fix Fault Handling
    - Test Classes
Harrison 
    - Diagrams
    - Timing Performance
---------------------- Classes ----------------------

Elevator.java

    Elevator class that creates an elevator object which moves 
    from one floor to another based on the request. 

Motor.java
    
    The motor class is utilized to determine whether the elevator 
    should be moving or not.

Scheduler.java

    The Scheduler class takes requests from the floor subsystem 
    and executes each request. Executing the request entails commanding 
    the elevator to move to the floor where the request was made and then 
    commanding the elevator to move to the passenger's destination floor. 

Floor.java

    The floor class is used to simulate the arrival of passengers 
    to the elevators and simulating buttons being pressed. 

TimeConverter.java

    The time converter class is utilized to centralize all time
    time calculations. It converts the time string to ms or the time
    in ms to a proper string displayed in HH:mm:ss.ms.
    
FloorRequestHandler.java

    The FloorRequestHandler class receives UDP packets from 
    the floor subsystem with the request and stores it in a list.
    It provides a method to access the first request in the list.

ElevatorController.java
    
    ElevatorController is in charge of controlling its Elevator to fulfill the request of the passenger.
    ElevatorController communicates by receiving and sending  UDP messages to a well known port on it's Elevator.

GUI.java

    The GUI creates a window that will display the elevator positions. 
    Along with which direction they are going and when they fail.
 
ElevatorControllerTest.java
    
    Test class for the ElevatorController class. It is testing if it the information from
    the elevator is correct.

FloorRequestHandlerTest.java
    
    Test class for the FloorRequestHandler class. It is testing if it receives the proper requests

ElevatorTest.java
    
    Test class for the Elevator class. It is testing if the elevator moves properly,
    as well as if it is going the proper direction.

MotorTest.java
    
    Test class for the Motor class. It is testing if the is on or off.

---------------------- Instructions ----------------------

(For less wait time for outputs, you can change the values of 
TIME_BETWEEN_EACH_FLOOR and TIME_TO_OPEN_CLOSE to something smaller.)

1. Run Elevator.java
2. Run Scheduler.java
3. Run Floor.java