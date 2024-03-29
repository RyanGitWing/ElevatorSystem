package Tests;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

import Subsystems.Floor;
import Subsystems.FloorRequestHandler;

/**
 * Floor Request Handler Test Case.
 *
 * @author Group2
 * @version April 12, 2022
 */
public class FloorRequestHandlerTest {

	@Test
	public void testGetRequest() {
		Thread f = new Thread(new Floor());
		FloorRequestHandler requestHandler = new FloorRequestHandler(4999);
		
		f.start();
		requestHandler.receiveRequest();
		
		int[] request = requestHandler.getRequest();
		assertEquals(request[1], 1);
		assertEquals(request[2], 1);
		assertEquals(request[3], 22);
	}
}
