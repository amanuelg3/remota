package jp.gr.java_conf.remota.android;

/**
 * This is the mouse event class
 */
public class KeyboardEvent extends InputEvent {
	// Constants that indicate the keyboard event flags
	public static final int FLAG_KEYDOWN = 0x0000;
	public static final int FLAG_kEYUP   = 0x0002;
	
	// Member fields
	private int mFlag;
	private int mScanCode;
	
	/**
	 * 
	 * @param flag
	 * @param virtualCode
	 */
	public KeyboardEvent(int flag, int scanCode) {
		super(InputEvent.TYPE_KEYBOARD);
		
		mFlag = flag;
		mScanCode = scanCode;
	}

	/**
	 * 
	 * @return
	 */
	public int getFlag() {
		return mFlag;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getScanCode() {
		return mScanCode;
	}
}