import java.util.concurrent.TimeUnit;

public final class TimeConverter {
	
	public static int timeToMS(String time) {
		
		String[] hourMinSec = time.split(":");
		String[] secMs = hourMinSec[2].split("\\.");
		
		int convertedTime = Integer.parseInt(hourMinSec[0])*3600000 + Integer.parseInt(hourMinSec[1])*60000 + 
		                        Integer.parseInt(secMs[0])*1000 + Integer.parseInt(secMs[1]);
		
		return convertedTime;
	}
	
	public static String msToTime(int timeInMS){
		String timeString = String.format("%02d:%02d:%02d.%d", TimeUnit.MILLISECONDS.toHours(timeInMS),TimeUnit.MILLISECONDS.toMinutes(timeInMS)%60,
				TimeUnit.MILLISECONDS.toSeconds(timeInMS)%60,TimeUnit.MILLISECONDS.toMillis(timeInMS)%1000);

		return timeString;
	}

}
