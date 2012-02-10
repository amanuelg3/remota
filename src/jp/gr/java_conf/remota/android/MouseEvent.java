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
public class MouseEvent extends InputEvent {
	// Constants that indicate the mouse event flags
	public static final int FLAG_MOVE        = 0x0001;
	public static final int FLAG_WHELL       = 0x0800;
	public static final int FLAG_LEFT_UP     = 0x0004;
	public static final int FLAG_LEFT_DOWN   = 0x0002;
	public static final int FLAG_RIGHT_UP    = 0x0010;
	public static final int FLAG_RIGHT_DOWN  = 0x0008;
	public static final int FLAG_MIDDLE_UP   = 0x0040;
	public static final int FLAG_MIDDLE_DOWN = 0x0020;
	public static final int FLAG_X_UP        = 0x0100;
	public static final int FLAG_X_DOWN      = 0x0080;
	public static final int FLAG_ABSOLUTE    = 0x8000;
	public static final int FLAG_VIRTUALDESK = 0x4000;
	public static final int FLAG_HWHEEL      = 0x1000;
	
	// Member fields
	private int mFlag;
	private int mX;
	private int mY;
	private int mWheel;
	
	/**
	 * 
	 * @param flag
	 * @param x 
	 * @param y
	 */
	public MouseEvent(int flag, int x, int y, int wheel) {
		super(InputEvent.TYPE_MOUSE);
		mFlag = flag;
		mX = x;
		mY = y;
		mWheel = wheel;
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
	
	/**
	 * 
	 * @return
	 */
	public int getWheel() {
		return mWheel;
	}
}
