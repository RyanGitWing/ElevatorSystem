import java.util.ArrayList;

/**
 * The Scheduler class stores an instruction given by
 * the floor or elevator subsystem that is available
 * to be acquired by these subsystems. 
 * 
 * @author Aleksandar Veselinovic
 * @version 1.0
 */
public class Scheduler{
	
	private ArrayList<int[]> instructions;
	private boolean isEmpty;
	
	/**
	 * Constructor of a Scheduler object
	 */
	public Scheduler() {
		instructions = new ArrayList<int[]>();
		isEmpty = true;
	}
	
	/**
	 * This method putInstructions an instruction in the Scheduler object.
	 * Once the instruction is putInstruction in the Scheduler object this method returns.
	 * 
	 * @param instruction The instruction to putInstruction in the Scheduler object
	 */
	public synchronized void putInstruction(int[] instruction) {
		while (!isEmpty) {
			try {
				wait();
			} catch (InterruptedException e) {
				return;
			}
		}
		instructions.add(instruction);
		isEmpty = false;
		notifyAll();
	}
	
	/**
	 * This method takes the instruction from the Scheduler object.
	 * Once the instruction is taken this method returns.
	 * 
	 * @return The instruction taken from the Scheduler object
	 */
	public synchronized int[] getInstruction() {
		while (isEmpty) {
			try {
				wait();
			} catch (InterruptedException e) {
				return null;
			}
		}
		int[] nextInstruction = instructions.remove(0);
		isEmpty = true;
		notifyAll();
		return nextInstruction;
	}

}
