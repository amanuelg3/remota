package jp.gr.java_conf.remota.android;

import android.content.Context;
import android.graphics.Canvas;
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
		if (DBG) Log.i(TAG, "height:" + canvas.getHeight() + ", width:" + canvas.getWidth());
		holder.unlockCanvasAndPost(canvas);
	}
	
	/**
	 * 
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		if (DBG) Log.i(TAG, "+++ SERFACE CREATED +++");
		
		Canvas canvas = holder.lockCanvas();
		if (DBG) Log.i(TAG, "height:" + canvas.getHeight() + ", width:" + canvas.getWidth());
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
		if (DBG) Log.d(TAG, "X:" + event.getRawX() + ",Y:" + event.getRawY());
		return true;
	}
}
