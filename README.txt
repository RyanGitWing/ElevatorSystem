---------------------- Description ----------------------

Elevator system that reads and process an instruction file.
The floor acts as the client which instructs the elevator on
where to go pick up an individual. The scheduler acts as the 
middle which takes the information provided by the floor and
delivers it to the elevator. The elevator, also a client,
gets the information and follows the instructions.

Currently, in this iteration, the elevator moves based on the 
instructions received from the scheduler. The scheduler receives
these instructions from the floor based on the input file. Once
the scheduler has the instruction, it determines order the 
elevator moves in, what direction the elevator moves in, as well 
as the floor that it will be moving towards. The elevator then 
receives the instructions and moves based on those directions.

---------------------- Responsiblities ----------------------

A lot of the work was done together in a meeting with every 
member. Everyone was providing input and aiding in the coding 
and design process. For the sake of dividing some of the work,
each person was assigned tasks.

Tyler
	- TimeConverter Class
	- README File

Aleksandar
	- Scheduler Class
	- SchedulerTest Class

Dominique
	- UML Diagrams
	- Sequence Diagrams

Ryan
	- Elevator Class
	- ElevatorTest Class
	- FloorTest Class

Harrison
	- State Diagrams
	- Math File

---------------------- Classes ----------------------

Elevator.java

	Elevator class that creates an elevator object which moves
 	from one floor to another based on the request. Using a motor,
	it moves from floor to floor with a set time to move and time 
	to open based on the instruction received from the scheduler.

Motor.java
	
	The motor class is utilized to determine whether the elevator 
	should be moving or not.

Scheduler.java

	The scheduler class is a monitor for the list of instructions
 	for the elevator control system. An instruction can be put into
 	the scheduler and the first instruction in the list can be taken
 	from the scheduler. The scheduler also tells the elevator when to
	move and stop based on the instruction read. 

Floor.java

	The floor class is used to simulate the arrival of passengers
 	to the elevators and simulating buttons being pressed. These instructions
	get sent to the scheduler.

TimeConverter.java

	The time converter class is utilized to centralize all time
	time calculations. It converts the time string to ms or the time
	in ms to a proper string displayed in HH:mm:ss.ms.
 	 
SchedulerTest.java
	
	Test class for the Scheulder class. It is testing if it can properly 
	send the instruction to the elevator.

FloorTest.java
	
	Test class for the Floor class. It is testing if it can properly read the
	input file and store it.

ElevatorTest.java
	
	Test class for the Elevator class. It is testing if the elevator moves properly,
	as well as if it is going the proper direction.

Main.java

	The main class is used to run the elevator control system simulator. It creates
	three threads, one for the floor, one for the elevator and one for the scheduler.

---------------------- Instructions ----------------------

(For less wait time for outputs, you can change the values of 
TIME_BETWEEN_EACH_FLOOR and TIME_TO_OPEN_CLOSE to something smaller.)

1. Run Main.java