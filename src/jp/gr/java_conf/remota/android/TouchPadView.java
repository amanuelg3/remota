package jp.gr.java_conf.remota.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/** 
 * This is the SurfaceView like a touch pad.
 */
public class TouchPadView extends SurfaceView implements SurfaceHolder.Callback {
	// Debugging
	private static final String TAG = "TouchPadView";
	private static final boolean DBG = true;
	
	// Constants
	private static final float BUTTON_HEIGHT_RATIO = 0.2f;
	private static final float SCROLLBAR_WIDTH_RATIO = 0.08f;
	private static final float KEYBOARD_BUTTON_HEIGHT_RATIO = 0.05f;
	
	// Member fields
	private float mCanvasHeight = 0.0f;
	private float mCanvasWidth  = 0.0f;

	/**
     * Constructor
     * @param context
     * @param remotaService
     */
	public TouchPadView(Context context) {
		super(context);
		
		// Set up to receive surface events
		getHolder().addCallback(this);
	}
	
	/**
	 * Called when the surface is changed.
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if (DBG) Log.i(TAG, "+++ SURFACE CHANGED +++");
		
		Canvas canvas = holder.lockCanvas();
		mCanvasHeight = canvas.getHeight();
		mCanvasWidth  = canvas.getWidth();
		
		drawAll(canvas);
		
		if (DBG) Log.i(TAG, "height:" + canvas.getHeight() + ", width:" + canvas.getWidth());
		holder.unlockCanvasAndPost(canvas);
	}
	
	/**
	 * Called when the surface is created.
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		if (DBG) Log.i(TAG, "+++ SURFACE CREATED +++");
		
		Canvas canvas = holder.lockCanvas();
		mCanvasHeight = canvas.getHeight();
		mCanvasWidth  = canvas.getWidth();
		
		drawAll(canvas);
		
		if (DBG) Log.i(TAG, "height:" + canvas.getHeight() + ", width:" + canvas.getWidth());
		holder.unlockCanvasAndPost(canvas);
	}
	
	/**
	 * Called when the surface is destroyed. 
	 */
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (DBG) Log.i(TAG, "+++ SURFACE DESTROYED +++");	
	}
	
	// Return the left button rectangle.
	public RectF getLeftButtonRectF() {
		RectF rectf = new RectF(
				0.0f,
				0.0f,
				mCanvasWidth * (0.5f - (SCROLLBAR_WIDTH_RATIO / 2.0f)),
				mCanvasHeight * BUTTON_HEIGHT_RATIO
		);
		
		return rectf;
	}
	
	// Return the right button rectangle.
	public RectF getRightButtonRectF() {
		RectF rectf = new RectF(
				mCanvasWidth * (0.5f - (SCROLLBAR_WIDTH_RATIO / 2.0f)),
				0.0f,
				mCanvasWidth * (1.0f - SCROLLBAR_WIDTH_RATIO),
				mCanvasHeight * BUTTON_HEIGHT_RATIO
		);
		
		return rectf;
	}
	
	// Return the Scroll rectangle.
	public RectF getScrollBarRectF() {
		RectF rectf = new RectF(
				mCanvasWidth * (1.0f - SCROLLBAR_WIDTH_RATIO),
				0.0f,
				mCanvasWidth,
				mCanvasHeight
		);
		
		return rectf;
	}
	
	// Return the keyboard button rectangle.
	public RectF getKeyboardButtonRectF() {
		RectF rectf = new RectF(
				0.0f,
				mCanvasHeight * (1.0f - KEYBOARD_BUTTON_HEIGHT_RATIO),
				mCanvasWidth * (1.0f - SCROLLBAR_WIDTH_RATIO),
				mCanvasHeight
		);
		
		return rectf;
	}
	
	// Return the touch pad rectangle.
	public RectF getTouchPadRectF() {
		RectF rectf = new RectF(
				0.0f,
				mCanvasHeight * BUTTON_HEIGHT_RATIO,
				mCanvasWidth * (1.0f - SCROLLBAR_WIDTH_RATIO),
				mCanvasHeight * (1.0f - KEYBOARD_BUTTON_HEIGHT_RATIO)
		);
		
		return rectf;
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
	
	// Draw all components
	private void drawAll(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(getLeftButtonRectF(), paint);
		canvas.drawRect(getRightButtonRectF(), paint);
		canvas.drawRect(getScrollBarRectF(), paint);
		canvas.drawRect(getKeyboardButtonRectF(), paint);
		paint.setStyle(Paint.Style.FILL);
		canvas.drawRect(getTouchPadRectF(), paint);
	}
}
