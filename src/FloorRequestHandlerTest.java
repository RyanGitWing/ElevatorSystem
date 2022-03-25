import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

/**
 * Floor Request Handler Test Case.
 *
 * @author Ryan Nguyen
 * @version March 12, 2022
 */
public class FloorRequestHandlerTest {

	@Test
	public void testGetRequest() {
		Thread f = new Thread(new Floor());
		FloorRequestHandler requestHandler = new FloorRequestHandler(4999);
		Thread requestHandlerThread = new Thread(requestHandler);
		
		f.start();
		requestHandlerThread.start();
		
		int[] request = requestHandler.getRequest();
		assertEquals(request[1], 2);
		assertEquals(request[2], 1);
		assertEquals(request[3], 4);
	}
}
