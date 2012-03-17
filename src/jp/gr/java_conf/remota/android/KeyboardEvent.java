/* Copyright (C) 2011-2012 Test Muroi (test.muroi@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 */

package jp.gr.java_conf.remota.android;

/**
 * This is the mouse event class
 */
public class KeyboardEvent extends InputEvent {
	// Constants that indicate the keyboard event flags
	public static final int FLAG_KEYDOWN = 0x0000;
	public static final int FLAG_kEYUP   = 0x0002;
	
	// Constants that indicate the scancode
	public static final int SCANCODE_SHIFT = 42;
	public static final int SCANCODE_CTRL  = 29;
	public static final int SCANCODE_ALT   = 56;
	public static final int SCANCODE_DONE  = -3;
	public static final int SCANCODE_FN    = -2;
	
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