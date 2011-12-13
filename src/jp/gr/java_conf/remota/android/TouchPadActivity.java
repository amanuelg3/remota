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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * 
 */
public class TouchPadActivity extends Activity implements View.OnTouchListener {
	// Debugging
	private static final String TAG = "TouchPadActivity";
	private static final boolean DBG = false;
	
	// Constants
	private static final long THRESHOLD_TIME_FOR_TAP = 100l; // [msec]
	
	// Intent request codes
	private static final int REQUEST_SHOW_KEYBOARD = 1;
	private static final int REQUEST_SHOW_HELP = 2;
	
	// Member fields
	private TouchPadView mTouchPadView;
	private TouchState mTouchState;
	private long mMovePadDownTime = 0l;
	
	// The BroadcastReceiver that listens for disconnection
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
			if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
				if (DBG) Log.d(TAG, "disconnect!");
				TouchPadActivity.this.finish();
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
		
		// Set the screen orientation
		String orientation = sp.getString(getString(R.string.touch_pad_orientation_key), getString(R.string.orientation_auto));
		if (orientation.equals(getString(R.string.orientation_portrait_value))) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else if (orientation.equals(getString(R.string.orientation_landscape_value))) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else if (orientation.equals(getString(R.string.orientation_auto_value))) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		}

		mTouchPadView = new TouchPadView(this);
		
		mTouchState = TouchState.getInstance();

		// Set up to receive touch events
		mTouchPadView.setOnTouchListener(this);
		
		// Set up the window layout
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(mTouchPadView);
		
		// Register for broadcasts when device is disconnected
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		registerReceiver(mReceiver, filter);
		
		setResult(Activity.RESULT_OK);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
        
		if(DBG) Log.i(TAG, "+++ ON DESTROY +++");
		
		unregisterReceiver(mReceiver);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu_connected, menu);
		return true;
	}
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
   		case R.id.help_connected:
   			// Show help
   			Intent helpIntent = new Intent(this, HelpActivity.class);
   			helpIntent.putExtra(
   					HelpActivity.KEY_ANCHOR_LABEL,
   					getResources().getString(R.string.label_how_to_use_touch_pad)
   			);
   			startActivityForResult(helpIntent, REQUEST_SHOW_HELP);
    	}
    	return false;
	}
	
	/**
	 * Called when the view is touched.
	 */
	public boolean onTouch(View view, MotionEvent event) {
		if (view == mTouchPadView) {
			if (DBG) Log.d(TAG, "Pointer count:" + event.getPointerCount());
			
			RemotaService service = RemotaService.getInstance();
			int x, y;
			float fx, fy;
			int id, actionMasked, actionId;

			// the action information without the pointer index
			actionMasked = event.getActionMasked();
		
			// the index of the pointer that has gone down or up
			actionId = event.getActionIndex();

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
				if (TouchPadView.pointFIsInRectF(point, mTouchPadView.getLeftButtonRectF())) {
					if (mTouchState.getLeftButtonState() == TouchState.NOT_PRESSED) {
						mTouchState.setLeftButtonState(id);
						service.sendMouseEvent(
								new MouseEvent(MouseEvent.FLAG_LEFT_DOWN, 0, 0, 0)
						);
					}
				} else if (TouchPadView.pointFIsInRectF(point, mTouchPadView.getRightButtonRectF())) {
					if (mTouchState.getRightButtonState() == TouchState.NOT_PRESSED) {
						mTouchState.setRightButtonState(id);
						service.sendMouseEvent(
								new MouseEvent(MouseEvent.FLAG_RIGHT_DOWN, 0, 0, 0)
						);
					}
				} else if (TouchPadView.pointFIsInRectF(point, mTouchPadView.getScrollBarRectF())) { 
					if (mTouchState.getScrollBarState() == TouchState.NOT_PRESSED) {
						mTouchState.setScrollBarState(id);
						mTouchState.setPrevWheelY(y);
					}
				} else if (TouchPadView.pointFIsInRectF(point, mTouchPadView.getKeyboardButtonRectF())) { 
					if (mTouchState.getKeyboardButtonState() == TouchState.NOT_PRESSED) {
						//mTouchState.setKeyboardButtonState(id);
						Intent intent = new Intent(this, KeyboardActivity.class);
						startActivityForResult(intent, REQUEST_SHOW_KEYBOARD);
					}
				} else if (TouchPadView.pointFIsInRectF(point, mTouchPadView.getMovePadRectF())) {
					if (mTouchState.getMovePadState() == TouchState.NOT_PRESSED) {
						mTouchState.setMovePadState(id);
						mTouchState.setPrevX(x);
						mTouchState.setPrevY(y);
						
						// The event time for recognition of a tap
						mMovePadDownTime = event.getEventTime();
					}
				}
			// Check whether a touch up event occurs
			} else if (actionMasked == MotionEvent.ACTION_UP ||
					actionMasked == MotionEvent.ACTION_POINTER_1_UP ||
					actionMasked == MotionEvent.ACTION_POINTER_UP) {
				// Check where the touch up event occurs
				if (mTouchState.getLeftButtonState()== id) {
					mTouchState.setLeftButtonState(TouchState.NOT_PRESSED);
					service.sendMouseEvent(
							new MouseEvent(MouseEvent.FLAG_LEFT_UP, 0, 0, 0)
					);
				} else if (mTouchState.getRightButtonState() == id) {
					mTouchState.setRightButtonState(TouchState.NOT_PRESSED);
					service.sendMouseEvent(
							new MouseEvent(MouseEvent.FLAG_RIGHT_UP, 0, 0, 0)
					);
				} else if (mTouchState.getScrollBarState() == id) {
					mTouchState.setScrollBarState(TouchState.NOT_PRESSED);
				} else	if (mTouchState.getKeyboardButtonState() == id) {
					mTouchState.setKeyboardButtonState(TouchState.NOT_PRESSED);
				} else if (mTouchState.getMovePadState() == id) {
					mTouchState.setMovePadState(TouchState.NOT_PRESSED);
					
					// Check whether the event is a tap
					if ((event.getEventTime() - mMovePadDownTime) <= THRESHOLD_TIME_FOR_TAP) {
						// Send a mouse left button click event
						service.sendMouseEvent(
								new MouseEvent(MouseEvent.FLAG_LEFT_DOWN, 0, 0, 0)
						);
						
						service.sendMouseEvent(
								new MouseEvent(MouseEvent.FLAG_LEFT_UP, 0, 0, 0)
						);
					}
				}
			// Check whether a touch move event occurs
			} else if (actionMasked == MotionEvent.ACTION_MOVE) {
				if (mTouchState.getMovePadState() == id) {
					service.sendMouseEvent(
							new MouseEvent(
									MouseEvent.FLAG_MOVE,
									x - mTouchState.getPrevX(),
									y - mTouchState.getPrevY(),
									0
							)
					);
					mTouchState.setPrevX(x);
					mTouchState.setPrevY(y);
				} else if (mTouchState.getScrollBarState() == id) {
					service.sendMouseEvent(
							new MouseEvent(
									MouseEvent.FLAG_WHELL,
									0, 
									0, 
									-(y - mTouchState.getPrevWheelY())
							)
					);
					mTouchState.setPrevWheelY(y);
				}
			}
		}
		
		mTouchPadView.doDraw(mTouchPadView.getHolder());
		return true;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (DBG) Log.i(TAG, "+++ ON CONFIGURATION CHANGED +++");
		super.onConfigurationChanged(newConfig);
		
		mTouchPadView.doDraw(mTouchPadView.getHolder());
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (DBG) Log.i(TAG, "+++ ON ACTIVITY RESULT +++ :" + resultCode);
	}
}
