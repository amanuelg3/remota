package jp.gr.java_conf.remota.android;

public class KeyboardState {
	// Constants
	public static final int NOT_PRESSED = -1;

	// Class variables for Singleton
	private static KeyboardState sKeyboardState = null;
	
	// Member fields
	private int mPressedScanCode = NOT_PRESSED;

	private KeyboardState() {
	}
	
	/**
	 * 
	 * @return
	 */
	public static KeyboardState getInstance() {
		if (sKeyboardState == null) {
			sKeyboardState = new KeyboardState();
		}
		
		return sKeyboardState;
	}
	
	public void setPressedScanCode(int scanCode) {
		mPressedScanCode = scanCode;
	}
	
	public int getPressedScanCode() {
		return mPressedScanCode;
	}
}