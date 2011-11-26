package jp.gr.java_conf.remota.android;

import android.app.Activity;
import android.graphics.PointF;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

/**
 * 
 */
public class TouchPadActivity extends Activity implements View.OnTouchListener {
	// Debugging
	private static final String TAG = "TouchPadActivity";
	private static final boolean DBG = true;
	
	// Member fields
	private TouchPadView mTouchPadView;
	private KeyboardView mKeyboardView;
	private TouchState mTouchState;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(DBG) Log.i(TAG, "+++ ON CREATE +++");
		
		// To full screen
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		mTouchPadView = new TouchPadView(this);
		Keyboard keyboard = new Keyboard(this, R.xml.qwerty);
		mKeyboardView = new KeyboardView(this, null);
		mKeyboardView.setKeyboard(keyboard);
		
		mTouchState = TouchState.getInstance();

		// Set up to receive touch events
		mTouchPadView.setOnTouchListener(this);
		
		// Set up the window layout
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(mTouchPadView);
		//setContentView(mKeyboardView);
		
		setResult(Activity.RESULT_OK);
	}
	
	@Override	protected void onDestroy() {
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
			float x, y;
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
		
			x = event.getX(actionId);
			y = event.getY(actionId);
			id = event.getPointerId(actionId);
			PointF point = new PointF(x, y);

			// Check whether a button is down or up.
			if (actionMasked == MotionEvent.ACTION_DOWN ||
					actionMasked == MotionEvent.ACTION_POINTER_1_DOWN ||
					actionMasked == MotionEvent.ACTION_POINTER_DOWN) {
				if (TouchPadView.pointFIsInRectF(point, mTouchPadView.getLeftButtonRectF())) {
					if (mTouchState.getLeftButtonState() == TouchState.NOT_PRESSED) {
						mTouchState.setLeftButtonState(id);
						service.sendMouseEvent(
								new MouseEvent(MouseEvent.FLAG_LEFT_DOWN, (int)x, (int)y)
						);
					}
				} else if (TouchPadView.pointFIsInRectF(point, mTouchPadView.getRightButtonRectF())) {
					if (mTouchState.getRightButtonState() == TouchState.NOT_PRESSED) {
						mTouchState.setRightButtonState(id);
						service.sendMouseEvent(
								new MouseEvent(MouseEvent.FLAG_RIGHT_DOWN, (int)x, (int)y)
						);
					}
				} else if (TouchPadView.pointFIsInRectF(point, mTouchPadView.getScrollBarRectF())) { 
					if (mTouchState.getScrollBarState() == TouchState.NOT_PRESSED) {
						mTouchState.setScrollBarState(id);
						service.sendMouseEvent(
								new MouseEvent(MouseEvent.FLAG_WHELL, (int)x, (int)y)
						);
					}
				} else if (TouchPadView.pointFIsInRectF(point, mTouchPadView.getKeyboardButtonRectF())) { 
					if (mTouchState.getKeyboardButtonState() == TouchState.NOT_PRESSED) {
						mTouchState.setKeyboardButtonState(id);
					}
				} else if (TouchPadView.pointFIsInRectF(point, mTouchPadView.getTouchPadRectF())) {
					if (mTouchState.getMovePadState() == TouchState.NOT_PRESSED) {
						mTouchState.setMovePadState(id);
						service.sendMouseEvent(
								new MouseEvent(MouseEvent.FLAG_MOVE, (int)x, (int)y)
						);
					}
				}
			} else if (actionMasked == MotionEvent.ACTION_UP ||
					actionMasked == MotionEvent.ACTION_POINTER_1_UP ||
					actionMasked == MotionEvent.ACTION_POINTER_UP) {
				if (TouchPadView.pointFIsInRectF(point, mTouchPadView.getLeftButtonRectF())) {
					if (mTouchState.getLeftButtonState()== id) {
						mTouchState.setLeftButtonState(TouchState.NOT_PRESSED);
						service.sendMouseEvent(
								new MouseEvent(MouseEvent.FLAG_LEFT_UP, (int)x, (int)y)
						);
					}
				} else if (TouchPadView.pointFIsInRectF(point, mTouchPadView.getRightButtonRectF())) {
					if (mTouchState.getRightButtonState() == id) {
						mTouchState.setRightButtonState(TouchState.NOT_PRESSED);
						service.sendMouseEvent(
								new MouseEvent(MouseEvent.FLAG_RIGHT_UP, (int)x, (int)y)
						);
					}
				} else if (TouchPadView.pointFIsInRectF(point, mTouchPadView.getScrollBarRectF())) { 
					if (mTouchState.getScrollBarState() == id) {
						mTouchState.setScrollBarState(TouchState.NOT_PRESSED);
						service.sendMouseEvent(
								new MouseEvent(MouseEvent.FLAG_WHELL, (int)x, (int)y)
						);
					}
				} else if (TouchPadView.pointFIsInRectF(point, mTouchPadView.getKeyboardButtonRectF())) { 
					if (mTouchState.getKeyboardButtonState() == id) {
						mTouchState.setKeyboardButtonState(TouchState.NOT_PRESSED);
					}
				} else if (TouchPadView.pointFIsInRectF(point, mTouchPadView.getTouchPadRectF())) {
					if (mTouchState.getMovePadState() == id) {
						mTouchState.setMovePadState(TouchState.NOT_PRESSED);
						service.sendMouseEvent(
								new MouseEvent(MouseEvent.FLAG_MOVE, (int)x, (int)y)
						);
					}
				}
			} else if (actionMasked == MotionEvent.ACTION_MOVE) {
				if (TouchPadView.pointFIsInRectF(point, mTouchPadView.getTouchPadRectF())) {
					if (mTouchState.getMovePadState() == id) {
						service.sendMouseEvent(
								new MouseEvent(MouseEvent.FLAG_MOVE, (int)x, (int)y)
						);
					}
				}
			}
		
			// For debugging
			float hx, hy;
			for (int i = 0; i < c; i++){
				x = event.getX(i);
				y = event.getY(i);
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
							"X" + i + ":" + x +
							",Y" + i + ":" + y +
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
}
