---------------------- Description ----------------------

Elevator system that reads and process an instruction file.
The floor acts as the client which instructs the elevator on
where to go pick up an individual. The scheduler acts as the 
middle which takes the information provided by the floor and
delivers it to the elevator. The elevator, also a client,
gets the information and follows the instructions.

Currently, in this iteration, the elevator does not do anything.
It simply gets the instruction from the scheduler and send it 
back. This demonstrated that we were able to receive the 
information from the floor through the scheduler.

---------------------- Responsiblities ----------------------

Tyler
	- Floor Class

Aleksandar
	- Scheduler Class

Dominique
	- Diagrams
	- Math File
	- README File

Ryan
	- Elevator Class
	- SchedulerTest Class

---------------------- Classes ----------------------

Elevator.java

	Elevator class that creates an elevator object which moves
 	from one floor to another based on the request. 
	(currently does not move)

Scheduler.java

	The scheduler class is a monitor for the list of instructions
 	for the elevator control system. An instruction can be put into
 	the scheduler and the first instruction in the list can be taken
 	from the scheduler. 

Floor.java

	The floor class is used to simulate the arrival of passengers
 	to the elevators and simulating buttons being pressed.

SchedulerTest.java
	
	Test class for scheduler testing if it can properly receive the
	input file and store it.

Main.java

	Run the elevator control system simulator.

---------------------- Instructions ----------------------

1. Run Main.java