import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SchedulerTest {

	@Test
	void get() {
		Scheduler scheduler = new Scheduler();	
		
		int[] array = {123123, 2, 1, 4}; 
		
		scheduler.putInstruction(array);
		
		int[] temp = scheduler.getInstruction();
		
		for(int i = 0; i < temp.length; i++) {
			assertEquals(array[i], temp[i]);
		}
	}

}
