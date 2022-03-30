package Subsystems;
import java.util.concurrent.TimeUnit;

/**
 * The time converter class is utilized to do any time calculations. It converts
 * a string in the format of HH:mm:ss.ms to ms and from ms to a string in that 
 * same format.
 */
public final class TimeConverter {
	
	/**
	 * This converts the string in the format of HH:mm:ss.ms to milliseconds.
	 * @param time String in the format of HH:mm:ss.ms
	 * @return the converted time in ms
	 */
	public static int timeToMS(String time) {
		
		//String arrays containing each part of the string divided
		String[] hourMinSec = time.split(":");
		String[] secMs = hourMinSec[2].split("\\.");
		
		//time conversion calculation from string to milliseconds
		int convertedTime = Integer.parseInt(hourMinSec[0])*3600000 + Integer.parseInt(hourMinSec[1])*60000 + 
		                        Integer.parseInt(secMs[0])*1000 + Integer.parseInt(secMs[1]);
		
		return convertedTime;
	}
	
	/**
	 * This converts the time in milliseconds to the formatted string.
	 * @param timeInMS integer containing the time in ms
	 * @return the formatted string (HH:mm:ss.ms)
	 */
	public static String msToTime(int timeInMS){

		//time conversion calculation from integer to the formatted string
		String timeString = String.format("%02d:%02d:%02d.%d", TimeUnit.MILLISECONDS.toHours(timeInMS),TimeUnit.MILLISECONDS.toMinutes(timeInMS)%60,
				TimeUnit.MILLISECONDS.toSeconds(timeInMS)%60,TimeUnit.MILLISECONDS.toMillis(timeInMS)%1000);

		return timeString;
	}

}
