/* Copyright (C) 2011-2012 Test Muroi (test.muroi@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 */

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
	
	private int mPrevX;
	private int mPrevY;
	private int mPrevWheelY;
	private float mPrevFX;
	private float mPrevFY;
	
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
	
	/**
	 * 
	 * @return
	 */
	public int getPrevX() {
		return mPrevX;
	}
	
	/**
	 * 
	 * @param prevX
	 */
	public synchronized void setPrevX(int prevX) {
		mPrevX = prevX;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getPrevY() {
		return mPrevY;
	}
	
	/**
	 * 
	 * @param prevY
	 */
	public synchronized void setPrevY(int prevY) {
		mPrevY = prevY;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getPrevWheelY() {
		return mPrevWheelY;
	}
	
	/**
	 * 
	 * @param prevY
	 */
	public synchronized void setPrevWheelY(int prevWheelY) {
		mPrevWheelY = prevWheelY;
	}
	
	/**
	 * 
	 * @return
	 */
	public float getPrevFX() {
		return mPrevFX;
	}
	
	/**
	 * 
	 * @param prevY
	 */
	public synchronized void setPrevFX(float prevFX) {
		mPrevFX = prevFX;
	}
	
	/**
	 * 
	 * @return
	 */
	public float getPrevFY() {
		return mPrevFY;
	}
	
	/**
	 * 
	 * @param prevY
	 */
	public synchronized void setPrevFY(float prevFY) {
		mPrevFY = prevFY;
	}
	
	
}
