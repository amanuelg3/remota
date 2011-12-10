package jp.gr.java_conf.remota.android;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * 
 */
public class MotionPadActivity extends Activity implements View.OnTouchListener, SensorEventListener {
	// Debugging
	private static final String TAG = "MotionPadActivity";
	private static final boolean DBG = true;
	
	// Intent request codes
	private static final int REQUEST_SHOW_KEYBOARD = 1;
	
	// Member fields
	private MotionPadView mMotionPadView;
	private TouchState mTouchState;
	private SensorManager mSensorManager;
	private Sensor mGyroSensor;
	private boolean mNoPrevData = true;
	private boolean mOnMoveMode = false;
	private float mPrevGX = 0.0f;
	private float mPrevGZ = 0.0f;
	private long mPrevTimeStamp = 0l;
	
	// The BroadcastReceiver that listens for disconnection
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
			if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
				if (DBG) Log.d(TAG, "disconnect!");
				MotionPadActivity.this.finish();
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (DBG) Log.i(TAG, "+++ ON CREATE +++");
		
		// To full screen
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		if (sp.getBoolean(getString(R.string.fullscreen_key), false)) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		
		// Set the screen orientation to portrait
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		mMotionPadView = new MotionPadView(this);
		
		mTouchState = TouchState.getInstance();
		
		// Set up to listen gyro sensor events
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		if (mSensorManager.getSensorList(Sensor.TYPE_GYROSCOPE).isEmpty()) {
			// if no gyro sensors, then finish 
			setResult(Activity.RESULT_CANCELED);
			finish();
		}
		mGyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		startListenSensor();

		// Set up to receive touch events
		mMotionPadView.setOnTouchListener(this);
		
		// Set up the window layout
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(mMotionPadView);
		
		// Register for broadcasts when device is disconnected
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		registerReceiver(mReceiver, filter);
		
		setResult(Activity.RESULT_OK);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		if(DBG) Log.i(TAG, "+++ ON START +++");
		
		startListenSensor();
	}
	
	@Override
	protected synchronized void onResume() {
		super.onResume();
        
		if(DBG) Log.i(TAG, "+++ ON RESUME +++");
		
		startListenSensor();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(DBG) Log.i(TAG, "+++ ON PAUSE +++");
		
		stopListenSensor();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		if(DBG) Log.i(TAG, "+++ ON STOP +++");
		
		stopListenSensor();	
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
        
		if(DBG) Log.i(TAG, "+++ ON DESTROY +++");
		
		stopListenSensor();
		unregisterReceiver(mReceiver);
	}
	
	/**
	 * Called when the view is touched.
	 */
	public boolean onTouch(View view, MotionEvent event) {
		if (view == mMotionPadView) {
			if (DBG) Log.d(TAG, "Pointer count:" + event.getPointerCount());
			
			RemotaService service = RemotaService.getInstance();
			int x, y;
			float fx, fy;
			int id, action, actionMasked, actionId;
		
			// the action information
			action = event.getAction();
		
			// the action information without the pointer index
			actionMasked = event.getActionMasked();
		
			// the index of the pointer that has gone down or up
			actionId = event.getActionIndex();

			// For debugging
			if (DBG) {
				Log.d(TAG, 
						", action:" + action +
						", actionMasked:" + actionMasked +
						", actionId:" + actionId +
						", history:" + event.getHistorySize()
				);
			}
			
			fx = event.getX(actionId);
			fy = event.getY(actionId);
			x = (int)(fx * event.getXPrecision());
			y = (int)(fy * event.getYPrecision());
			id = event.getPointerId(actionId);
			PointF point = new PointF(fx, fy);

			// Check whether a touch down event occurs 
			if (actionMasked == MotionEvent.ACTION_DOWN ||
					actionMasked == MotionEvent.ACTION_POINTER_1_DOWN ||
					actionMasked == MotionEvent.ACTION_POINTER_DOWN) {
				// Check where the touch down event occurs
				if (PadView.pointFIsInRectF(point, mMotionPadView.getLeftButtonRectF())) {
					if (mTouchState.getLeftButtonState() == TouchState.NOT_PRESSED) {
						mTouchState.setLeftButtonState(id);
						service.sendMouseEvent(
								new MouseEvent(MouseEvent.FLAG_LEFT_DOWN, 0, 0, 0)
						);
					
						// Turn on the move mode
						mOnMoveMode = true;
					}
				} else if (PadView.pointFIsInRectF(point, mMotionPadView.getRightButtonRectF())) {
					if (mTouchState.getRightButtonState() == TouchState.NOT_PRESSED) {
						mTouchState.setRightButtonState(id);
						service.sendMouseEvent(
								new MouseEvent(MouseEvent.FLAG_RIGHT_DOWN, 0, 0, 0)
						);
						
						// Turn on the move mode
						mOnMoveMode = true;
					}
				} else if (PadView.pointFIsInRectF(point, mMotionPadView.getScrollBarRectF())) { 
					if (mTouchState.getScrollBarState() == TouchState.NOT_PRESSED) {
						mTouchState.setScrollBarState(id);
						
						// Turn on the move mode
						mOnMoveMode = true;
					}
				} else if (PadView.pointFIsInRectF(point, mMotionPadView.getKeyboardButtonRectF())) { 
					if (mTouchState.getKeyboardButtonState() == TouchState.NOT_PRESSED) {
						mTouchState.setKeyboardButtonState(id);
						Intent intent = new Intent(this, KeyboardActivity.class);
						startActivityForResult(intent, REQUEST_SHOW_KEYBOARD);
					}
				} else if (PadView.pointFIsInRectF(point, mMotionPadView.getMovePadRectF())) {
					if (mTouchState.getMovePadState() == TouchState.NOT_PRESSED) {
						mTouchState.setMovePadState(id);
						
						// Turn on the move mode
						mOnMoveMode = true;
						
						mTouchState.setPrevFX(fx);
						mTouchState.setPrevFY(fy);
					}
				}
			// Check whether a touch up event occurs
			} else if (actionMasked == MotionEvent.ACTION_UP ||
					actionMasked == MotionEvent.ACTION_POINTER_1_UP ||
					actionMasked == MotionEvent.ACTION_POINTER_UP) {
				// Check where the touch up event occurs
				if (mTouchState.getLeftButtonState()== id) {
					mTouchState.setLeftButtonState(TouchState.NOT_PRESSED);
					mTouchState.setMovePadState(TouchState.NOT_PRESSED);
					service.sendMouseEvent(
							new MouseEvent(MouseEvent.FLAG_LEFT_UP, 0, 0, 0)
					);
				} else if (mTouchState.getRightButtonState() == id) {
					mTouchState.setRightButtonState(TouchState.NOT_PRESSED);
					mTouchState.setMovePadState(TouchState.NOT_PRESSED);
					service.sendMouseEvent(
							new MouseEvent(MouseEvent.FLAG_RIGHT_UP, 0, 0, 0)
					);
				} else if (mTouchState.getScrollBarState() == id) {
					mTouchState.setScrollBarState(TouchState.NOT_PRESSED);
					mTouchState.setMovePadState(TouchState.NOT_PRESSED);
				} else	if (mTouchState.getKeyboardButtonState() == id) {
					mTouchState.setKeyboardButtonState(TouchState.NOT_PRESSED);
					mTouchState.setMovePadState(TouchState.NOT_PRESSED);
				} else if (mTouchState.getMovePadState() == id) {
					mTouchState.setMovePadState(TouchState.NOT_PRESSED);
				}
				
				if (mTouchState.getLeftButtonState() == TouchState.NOT_PRESSED &&
						mTouchState.getRightButtonState() == TouchState.NOT_PRESSED &&
						mTouchState.getMovePadState() == TouchState.NOT_PRESSED &&
						mTouchState.getScrollBarState() == TouchState.NOT_PRESSED)
				{
					// all fingers are off, so clear previous data
					mNoPrevData = true;
					
					// Turn off the move mode
					mOnMoveMode = false;
				}
			// Check whether a touch move event occurs
			} else if (actionMasked == MotionEvent.ACTION_MOVE) {
				if (PadView.pointFIsInRectF(point, mMotionPadView.getLeftButtonRectF())) {
					if (mTouchState.getLeftButtonState() == TouchState.NOT_PRESSED) {
						mTouchState.setLeftButtonState(id);
						service.sendMouseEvent(
								new MouseEvent(MouseEvent.FLAG_LEFT_DOWN, 0, 0, 0)
						);
					}
				} else if (PadView.pointFIsInRectF(point, mMotionPadView.getRightButtonRectF())) {
					if (mTouchState.getRightButtonState() == TouchState.NOT_PRESSED) {
						mTouchState.setRightButtonState(id);
						service.sendMouseEvent(
								new MouseEvent(MouseEvent.FLAG_RIGHT_DOWN, 0, 0, 0)
						);
					}
				} else if (PadView.pointFIsInRectF(point, mMotionPadView.getScrollBarRectF())) { 
					if (mTouchState.getScrollBarState() == TouchState.NOT_PRESSED) {
						mTouchState.setScrollBarState(id);
					}
				} else if (PadView.pointFIsInRectF(point, mMotionPadView.getKeyboardButtonRectF())) { 
					if (mTouchState.getKeyboardButtonState() == TouchState.NOT_PRESSED) {
						mTouchState.setKeyboardButtonState(id);
						Intent intent = new Intent(this, KeyboardActivity.class);
						startActivityForResult(intent, REQUEST_SHOW_KEYBOARD);
					}
				}
			}
		}
		
		mMotionPadView.doDraw(mMotionPadView.getHolder());
		return true;
	}
	
	/**
	 * Called when the accuracy of a sensor has changed.
	 */
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		if (DBG) Log.i(TAG, "+++ ON ACCURACY CHANGED +++");
	}
	
	/**
	 * Called when sensor values have changed.
	 */
	public void onSensorChanged(SensorEvent sensorEvent) {
		if (mGyroSensor != sensorEvent.sensor) {
			if (DBG) Log.e(TAG, "unknown sensor event occurs");
		} else if (sensorEvent.values.length != 3) {
			if (DBG) Log.e(TAG, "the number of sensor values is not 3" );
		} else if (mOnMoveMode == false){
			// do nothing
		} else {
			long time = sensorEvent.timestamp;
			float gx = sensorEvent.values[0]; // [rad/sec]
			float gz = sensorEvent.values[2]; // [rad/sec]
			
			// Check previous gyro sensor data.
			if (mNoPrevData) {
				mNoPrevData = false;
				mPrevGX = gx;
				mPrevGZ = gz;
				mPrevTimeStamp = time;
			} else {
				RemotaService service = RemotaService.getInstance();
				
				// Calculate changes of angles from prevTimeStamp to now
				float deltaTime = (time - mPrevTimeStamp) * 0.000000001f; // [nano sec] -> [sec]
				float deltaX = (mPrevGX + gx) * deltaTime / 2.0f; // [rad]
				float deltaZ = (mPrevGZ + gz) * deltaTime / 2.0f; // [rad]
				deltaX = (float)(deltaX * 180.0f / Math.PI); // [rad] -> [deg]
				deltaZ = (float)(deltaZ * 180.0f / Math.PI); // [rad] -> [deg]
				deltaX *= 10.0f;
				deltaZ *= 10.0f;
				
				if (DBG) {
					Log.i(TAG, "dt:" + deltaTime + ",dx:" + deltaX + ",dz:" + deltaZ);
				}
				
				MouseEvent event;
				if (mTouchState.getScrollBarState() != TouchState.NOT_PRESSED) {
					event = new MouseEvent(MouseEvent.FLAG_WHELL, 0, 0, (int)deltaX);
					service.sendMouseEvent(event);
				} else {
					event = new MouseEvent(MouseEvent.FLAG_MOVE, (int)(-deltaZ), (int)(-deltaX), 0);
					service.sendMouseEvent(event);
				}
				
				mPrevGX = gx;
				mPrevGZ = gz;
				mPrevTimeStamp = time;
			}
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (DBG) Log.i(TAG, "+++ ON CONFIGURATION CHANGED +++");
		super.onConfigurationChanged(newConfig);
		
		mMotionPadView.doDraw(mMotionPadView.getHolder());
	}
	
	/**
	 * 
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (DBG) Log.i(TAG, "+++ ON ACTIVITY RESULT +++ :" + resultCode);
		switch (requestCode) {
		case REQUEST_SHOW_KEYBOARD:
			mTouchState.setKeyboardButtonState(TouchState.NOT_PRESSED);
		}
	}
	
	private void startListenSensor() {
		mSensorManager.registerListener(this, mGyroSensor, SensorManager.SENSOR_DELAY_FASTEST);
	}
	
	private void stopListenSensor() {
		mSensorManager.unregisterListener(this, mGyroSensor);
	}
}
