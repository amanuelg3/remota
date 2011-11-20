package jp.gr.java_conf.remota.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/** 
 * This is the SurfaceView like a touch pad.
 */
public class TouchPadView extends SurfaceView implements View.OnTouchListener, SurfaceHolder.Callback {
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
		
		// Set up to receive touch events
		setOnTouchListener(this);
		
		// Set up to receive surface events
		getHolder().addCallback(this);
	}
	
	/**
	 * 
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if (DBG) Log.i(TAG, "+++ SERFACE CHANGED +++");
		
		Canvas canvas = holder.lockCanvas();
		mCanvasHeight = canvas.getHeight();
		mCanvasWidth  = canvas.getWidth();
		
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(getLeftButtonRect(), paint);
		canvas.drawRect(getRightButtonRect(), paint);
		canvas.drawRect(getScrollBarRect(), paint);
		canvas.drawRect(getKeyboardButtonRect(), paint);
		if (DBG) Log.i(TAG, "height:" + canvas.getHeight() + ", width:" + canvas.getWidth());
		holder.unlockCanvasAndPost(canvas);
	}
	
	/**
	 * 
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		if (DBG) Log.i(TAG, "+++ SERFACE CREATED +++");
		
		Canvas canvas = holder.lockCanvas();
		mCanvasHeight = canvas.getHeight();
		mCanvasWidth  = canvas.getWidth();
		
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(getLeftButtonRect(), paint);
		canvas.drawRect(getRightButtonRect(), paint);
		canvas.drawRect(getScrollBarRect(), paint);
		canvas.drawRect(getKeyboardButtonRect(), paint);
		if (DBG) Log.i(TAG, "height:" + mCanvasHeight + ", width:" + mCanvasWidth);
		holder.unlockCanvasAndPost(canvas);
	}
	
	/**
	 * 
	 */
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (DBG) Log.i(TAG, "+++ SERFACE DESTROYED +++");	
	}
	
	/**
	 * 
	 */
	public boolean onTouch(View view, MotionEvent event) {
		if (DBG) Log.d(TAG, "Pointer count:" + event.getPointerCount());
		int c = event.getPointerCount();
		
		for (int i = 0; i < c; i++){ 
			if (DBG) Log.d(TAG, "X" + i + ":" + event.getX(i) + ",Y" + i + ":" + event.getY(i));
		}
		
		return true;
	}
	
	private RectF getLeftButtonRect() {
		RectF rectf = new RectF(
				0.0f,
				0.0f,
				mCanvasWidth * (0.5f - (SCROLLBAR_WIDTH_RATIO / 2.0f)),
				mCanvasHeight * BUTTON_HEIGHT_RATIO
		);
		
		return rectf;
	}
	
	private RectF getRightButtonRect() {
		RectF rectf = new RectF(
				mCanvasWidth * (0.5f - (SCROLLBAR_WIDTH_RATIO / 2.0f)),
				0.0f,
				mCanvasWidth * (1.0f - SCROLLBAR_WIDTH_RATIO),
				mCanvasHeight * BUTTON_HEIGHT_RATIO
		);
		
		return rectf;
	}
	
	private RectF getScrollBarRect() {
		RectF rectf = new RectF(
				mCanvasWidth * (1.0f - SCROLLBAR_WIDTH_RATIO),
				0.0f,
				mCanvasWidth,
				mCanvasHeight
		);
		
		return rectf;
	}
	
	private RectF getKeyboardButtonRect() {
		RectF rectf = new RectF(
				0.0f,
				mCanvasHeight * (1.0f - KEYBOARD_BUTTON_HEIGHT_RATIO),
				mCanvasWidth * (1.0f - SCROLLBAR_WIDTH_RATIO),
				mCanvasHeight
		);
		
		return rectf;
	}
}
