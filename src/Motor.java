
public class Motor {
	
	private boolean isOn; 
	
	public Motor() {
		isOn = false;
	}
	
	public void toggleMotor(boolean isOn) {
		this.isOn = isOn;
	}
	
	public boolean getOn() {
		return isOn;
	}
}
