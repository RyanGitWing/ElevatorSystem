import java.util.ArrayList;

/**
 * The Scheduler class is a monitor for the list of instructions
 * for the elevator control system. An instruction can be put into
 * the scheduler and the first instruction in the list can be taken
 * from the scheduler. 
 * 
 * @author Aleksandar Veselinovic
 * @version February 5th, 2022
 */
public class Scheduler implements Runnable{
	
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
	 * This method adds an instruction to the end of the instruction list
	 * in the Scheduler object. Once the instruction is added to the list
	 * this method returns.
	 * 
	 * @param instruction The instruction to add to the instruction list
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
	 * This method takes the first instruction from the instruction list
	 * in the Scheduler object. Once the instruction is taken from the list
	 * this method returns.
	 * 
	 * @return The instruction taken from the instruction list
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
		isEmpty = instructions.isEmpty();
		notifyAll();
		return nextInstruction;
	}

	@Override
	public void run() {
	}

}
