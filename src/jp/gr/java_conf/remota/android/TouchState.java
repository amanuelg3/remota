package jp.gr.java_conf.remota.android;

/**
 * This is the touch state class 
 */
public class TouchState {
	// Constants
	public static final int NOT_PRESSED = -1;
	
	// Class variables for Singleton
	private static TouchState sTouchState = null;
	
	// Member fields
	private int mLeftButtonState     = NOT_PRESSED;
	private int mRightButtonState    = NOT_PRESSED;
	private int mScrollBarState      = NOT_PRESSED;
	private int mKeyboardButtonState = NOT_PRESSED;
	private int mMovePadState        = NOT_PRESSED;
	
	private TouchState() {
		
	}

	/**
	 * 
	 * @return
	 */
	public static TouchState getInstance() {
		if (sTouchState == null) {
			sTouchState = new TouchState();
		}
		
		return sTouchState;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getLeftButtonState() {
		return mLeftButtonState;
	}
	
	/**
	 * 
	 * @param state
	 */
	public synchronized void setLeftButtonState(int state) {
		mLeftButtonState = state;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getRightButtonState() {
		return mRightButtonState;
	}
	
	/**
	 * 
	 * @param state
	 */
	public synchronized void setRightButtonState(int state) {
		mRightButtonState = state;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getScrollBarState() {
		return mScrollBarState;
	}
	
	/**
	 * 
	 * @param state
	 */
	public synchronized void setScrollBarState(int state) {
		mScrollBarState = state;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getKeyboardButtonState() {
		return mKeyboardButtonState;
	}
	
	/**
	 * 
	 * @param state
	 */
	public synchronized void setKeyboardButtonState(int state) {
		mKeyboardButtonState = state;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getMovePadState() {
		return mMovePadState;
	}
	
	/**
	 * 
	 * @param state
	 */
	public synchronized void setMovePadState(int state) {
		mMovePadState = state;
	}
}
