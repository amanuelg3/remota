package jp.gr.java_conf.remota.android;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.PointF;
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
public class TouchPadActivity extends Activity implements View.OnTouchListener {
	// Debugging
	private static final String TAG = "TouchPadActivity";
	private static final boolean DBG = false;
	
	// Intent request codes
	private static final int REQUEST_SHOW_KEYBOARD = 1;
	
	// Member fields
	private TouchPadView mTouchPadView;
	private TouchState mTouchState;
	
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
		String orientation = sp.getString(getString(R.string.orientation_key), getString(R.string.orientation_system));
		if (orientation.equals(getString(R.string.orientation_portrait))) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else if (orientation.equals(getString(R.string.orientation_landscape))) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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
	}
	
	/**
	 * Called when the view is touched.
	 */
	public boolean onTouch(View view, MotionEvent event) {
		if (view == mTouchPadView) {
			if (DBG) Log.d(TAG, "Pointer count:" + event.getPointerCount());
			
			RemotaService service = RemotaService.getInstance();
			int c = event.getPointerCount();
			int x, y;
			float fx, fy;
			int id, action, actionMasked, actionId;
			String str = "";
		
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
				} else if (TouchPadView.pointFIsInRectF(point, mTouchPadView.getTouchPadRectF())) {
					if (mTouchState.getMovePadState() == TouchState.NOT_PRESSED) {
						mTouchState.setMovePadState(id);
						mTouchState.setPrevX(x);
						mTouchState.setPrevY(y);
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
		
			// For debugging
			float hx, hy;
			for (int i = 0; i < c; i++){
				fx = event.getX(i);
				fy = event.getY(i);
				id = event.getPointerId(i);
				point = new PointF(x, y);
			
				if (TouchPadView.pointFIsInRectF(point, mTouchPadView.getLeftButtonRectF())) {
					str = "left";
				}
				else if (TouchPadView.pointFIsInRectF(point, mTouchPadView.getRightButtonRectF())) {
					str = "right";
				}
				else if (TouchPadView.pointFIsInRectF(point, mTouchPadView.getScrollBarRectF())) {
					str = "scroll";
				}
				else if (TouchPadView.pointFIsInRectF(point, mTouchPadView.getKeyboardButtonRectF())) {
					str = "keyboard";
				}
				
				if (DBG) {
					Log.d(TAG, 
							"X" + i + ":" + fx +
							",Y" + i + ":" + fy +
							", id:" + id +
							"," + str);
				}
				
				if (DBG) {
					if (event.getHistorySize() >= 1) {
						hx = event.getHistoricalX(i, 1);
						hy = event.getHistoricalY(i, 1);
						Log.d(TAG, 
								"HX" + i + 1 + ":" + hx +
								"HY" + i + 1 + ":" + hy);
					}
				}
			}
		}
		
		mTouchPadView.doDraw(mTouchPadView.getHolder());
		return true;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (DBG) Log.i(TAG, "+++ ON ACTIVITY RESULT +++ :" + resultCode);
	}
}
