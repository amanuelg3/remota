/* Copyright (C) 2011-2012 Test Muroi (test.muroi@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 */

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