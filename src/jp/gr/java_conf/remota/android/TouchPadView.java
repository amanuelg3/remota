package jp.gr.java_conf.remota.android;

import android.content.Context;
import android.provider.Settings.SettingNotFoundException;
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
	
	// Member fields
	private RemotaService mRemotaService;
	
	/**
     * Constructor
     * @param context
     * @param remotaService
     */
	public TouchPadView(Context context, RemotaService remotaService) {
		super(context);
		
		mRemotaService = remotaService;
		
		setOnTouchListener(this);
	}
	
	/**
	 * 
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if (DBG) Log.i(TAG, "+++ SERFACE CHANGED +++");
	}
	
	/**
	 * 
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		if (DBG) Log.i(TAG, "+++ SERFACE CREATED +++");
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
