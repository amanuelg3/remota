package jp.gr.java_conf.remota.android;

/**
 * This is the input event class
 */
public class InputEvent {
	// Constants that indicate the input event types
	public static final int TYPE_MOUSE = 0xFFF0;
	public static final int TYPE_KEYBOARD = 0xFFF1;
	
	// Member fields
	private int mType;
	
	/**
	 * 
	 * @param type
	 */
	public InputEvent(int type) {
		mType = type;
	}

	/**
	 * 
	 * @return
	 */
	public int getType() {
		return mType;
	}
}