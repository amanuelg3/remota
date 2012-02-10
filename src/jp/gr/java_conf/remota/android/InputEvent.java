/* Copyright (C) 2011-2012 Test Muroi (test.muroi@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 */

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