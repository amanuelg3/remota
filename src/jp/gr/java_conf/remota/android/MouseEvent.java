package jp.gr.java_conf.remota.android;

/**
 * This is the mouse event class
 */
public class MouseEvent {
	// Constants that indicate the mouse event flags
	public static final int FLAG_MOVE = 0x01;
	public static final int FLAG_WHELL = 0x80;
	public static final int FLAG_LEFT_UP = 0x04;
	public static final int FLAG_LEFT_DOWN = 0x02;
	public static final int FLAG_RIGHT_UP = 0x10;
	public static final int FLAG_RIGHT_DOWN = 0x08;
	public static final int FLAG_MIDDLE_UP = 0x40;
	public static final int FLAG_MIDDLE_DOWN = 0x20;
	public static final int FLAG_X_UP = 0x200;
	public static final int FLAG_X_DOWN = 0x100;
	public static final int FLAG_ABSOLUTE = 0x8000;
	
	// Member fields
	private int mFlag;
	private int mX;
	private int mY;
	
	/**
	 * 
	 * @param flag
	 * @param x 
	 * @param y
	 */
	public MouseEvent(int flag, int x, int y) {
		mFlag = flag;
		mX = x;
		mY = y;
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
	public int getX() {
		return mX;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getY() {
		return mY;
	}
}