/* Copyright (C) 2011-2012 Test Muroi (test.muroi@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 */

package jp.gr.java_conf.remota.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/** 
 * This is the base class that implements mouse right/left/scroll/move buttons
 */
public abstract class PadView extends SurfaceView implements SurfaceHolder.Callback {
	// Member fields
	protected float mCanvasHeight = 0.0f;
	protected float mCanvasWidth  = 0.0f;
	protected TouchState mTouchState = null;
	protected int mBackgroundColor = Color.BLACK;
	
	/**
	 * Constructor
	 * @param context
	 */
	public PadView(Context context) {
		super(context);
		
		// Set up to receive surface events
		getHolder().addCallback(this);
	}
	
	/**
	 * Called when the surface is changed.
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Canvas canvas = holder.lockCanvas();
		mCanvasHeight = canvas.getHeight();
		mCanvasWidth  = canvas.getWidth();
		
		drawAll(canvas);
		
		holder.unlockCanvasAndPost(canvas);
	}
	
	/**
	 * Called when the surface is created.
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		mTouchState = TouchState.getInstance();
		
		Canvas canvas = holder.lockCanvas();
		mCanvasHeight = canvas.getHeight();
		mCanvasWidth  = canvas.getWidth();
		
		drawAll(canvas);
		
		holder.unlockCanvasAndPost(canvas);
	}
	
	/**
	 * Called when the surface is destroyed. 
	 */
	public void surfaceDestroyed(SurfaceHolder holder) {
	}
	
	public void doDraw(SurfaceHolder holder) {
		try {
			Canvas canvas = holder.lockCanvas();
			if (canvas != null) {
				drawAll(canvas);
			}
			holder.unlockCanvasAndPost(canvas);
		} catch (IllegalArgumentException e) {
		}
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		drawAll(canvas);
	}
	
	// Return true if the point is in the rectangle.
	public static boolean pointFIsInRectF(PointF point, RectF rect) {
		if (rect.left <= point.x && point.x <= rect.right) {
			if (rect.top <= point.y && point.y <= rect.bottom) { 
				return true;
			}
		}
		return false;
	}
	
	// Return the left button rectangle.
	public abstract RectF getLeftButtonRectF();
	
	// Return the right button rectangle.
	public abstract RectF getRightButtonRectF();
	
	// Return the Scroll rectangle.
	public abstract RectF getScrollBarRectF();
	
	// Return the keyboard button rectangle.
	public abstract RectF getKeyboardButtonRectF();
	
	// Return the touch pad rectangle.
	public abstract RectF getMovePadRectF();
	
	// Return the left button paint
	public abstract Paint getLeftButtonPaint();
	
	// Return the right button paint
	public abstract Paint getRightButtonPaint();
	
	// Return the scroll button paint
	public abstract Paint getScrollBarPaint();
	
	// Return the keyboard button paint
	public abstract Paint getKeyboardButtonPaint();
	
	// Return the touch pad paint
	public abstract Paint getMovePadPaint();
	
	// Return the stroke paint
	public abstract Paint getStrokePaint();
	
	// Draw all components
	protected void drawAll(Canvas canvas) {
		//canvas.drawColor(Color.TRANSPARENT);
		canvas.drawColor(mBackgroundColor);
		
		canvas.drawRect(getLeftButtonRectF(), getLeftButtonPaint());
		canvas.drawRect(getRightButtonRectF(), getRightButtonPaint());
		canvas.drawRect(getScrollBarRectF(), getScrollBarPaint());
		canvas.drawRect(getKeyboardButtonRectF(), getKeyboardButtonPaint());
		canvas.drawRect(getMovePadRectF(), getMovePadPaint());
		
		Paint paintS = getStrokePaint();
		canvas.drawRect(getLeftButtonRectF(), paintS);
		canvas.drawRect(getRightButtonRectF(), paintS);
		canvas.drawRect(getScrollBarRectF(), paintS);
		canvas.drawRect(getKeyboardButtonRectF(), paintS);
		canvas.drawRect(getMovePadRectF(), paintS);
	}
}