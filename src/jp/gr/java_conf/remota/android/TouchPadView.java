package jp.gr.java_conf.remota.android;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
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
	private static final int NOT_PRESSED = -1;
	
	// Member fields
	private float mCanvasHeight = 0.0f;
	private float mCanvasWidth  = 0.0f;
	
	private int mLeftButtonPressed     = NOT_PRESSED;
	private int mRightButtonPressed    = NOT_PRESSED;
	private int mScrollBarPressed      = NOT_PRESSED;
	private int mKeyboardButtonPressed = NOT_PRESSED;
	
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
	
	/**
	 * Called when the view is touched.
	 */
	public boolean onTouch(View view, MotionEvent event) {
		if (DBG) Log.d(TAG, "Pointer count:" + event.getPointerCount());

		RemotaService service = RemotaService.getInstance();
		int c = event.getPointerCount();
		float x, y;
		int id, action, actionMasked, actionId;
		ArrayList<Integer> idList = new ArrayList<Integer>();
		String str = "";
		action = event.getAction();
		actionMasked = event.getActionMasked();
		actionId = event.getActionIndex();
		
		if (DBG) {
			Log.d(TAG, 
					", action:" + action +
					", actionMasked:" + actionMasked +
					", actionId:" + actionId
			);
		}
		
		x = event.getX(actionId);
		y = event.getY(actionId);
		id = event.getPointerId(actionId);
		PointF point = new PointF(x, y);

		if (actionMasked == MotionEvent.ACTION_DOWN || actionMasked == MotionEvent.ACTION_POINTER_1_DOWN) {
			if (pointFIsInRectF(point, getLeftButtonRectF())) {
				if (mLeftButtonPressed == NOT_PRESSED) {
					mLeftButtonPressed = id;
					service.sendMouseEvent(
							new MouseEvent(MouseEvent.FLAG_LEFT_DOWN, (int)x, (int)y)
					);
				}
			} else if (pointFIsInRectF(point, getRightButtonRectF())) {
				if (mRightButtonPressed == NOT_PRESSED) {
					mRightButtonPressed = id;
					service.sendMouseEvent(
							new MouseEvent(MouseEvent.FLAG_RIGHT_DOWN, (int)x, (int)y)
					);
				}
			} else if (pointFIsInRectF(point, getScrollBarRectF())) { 
				if (mScrollBarPressed == NOT_PRESSED) {
					mScrollBarPressed = id;
					service.sendMouseEvent(
							new MouseEvent(MouseEvent.FLAG_WHELL, (int)x, (int)y)
					);
				}
			} else if (pointFIsInRectF(point, getKeyboardButtonRectF())) { 
				if (mKeyboardButtonPressed == NOT_PRESSED) {
					mKeyboardButtonPressed = id;
				}
			}
		} else if (actionMasked == MotionEvent.ACTION_UP || actionMasked == MotionEvent.ACTION_POINTER_1_UP) {
			if (pointFIsInRectF(point, getLeftButtonRectF())) {
				if (mLeftButtonPressed == id) {
					mLeftButtonPressed = NOT_PRESSED;
					service.sendMouseEvent(
							new MouseEvent(MouseEvent.FLAG_LEFT_UP, (int)x, (int)y)
					);
				}
			} else if (pointFIsInRectF(point, getRightButtonRectF())) {
				if (mRightButtonPressed == id) {
					mRightButtonPressed = NOT_PRESSED;
					service.sendMouseEvent(
							new MouseEvent(MouseEvent.FLAG_RIGHT_UP, (int)x, (int)y)
					);
				}
			} else if (pointFIsInRectF(point, getScrollBarRectF())) { 
				if (mScrollBarPressed == id) {
					mScrollBarPressed = NOT_PRESSED;
					service.sendMouseEvent(
							new MouseEvent(MouseEvent.FLAG_WHELL, (int)x, (int)y)
					);
				}
			} else if (pointFIsInRectF(point, getKeyboardButtonRectF())) { 
				if (mKeyboardButtonPressed == id) {
					mKeyboardButtonPressed = NOT_PRESSED;
				}
			}
		}

		for (int i = 0; i < c; i++){
			x = event.getX(i);
			y = event.getY(i);
			id = event.getPointerId(i);
			idList.add(new Integer(id));
			point = new PointF(x, y);
			
			// Check whether a button is down.
			if (pointFIsInRectF(point, getLeftButtonRectF())) {
				str = "left";
			}
			else if (pointFIsInRectF(point, getRightButtonRectF())) {
				str = "right";
			}
			else if (pointFIsInRectF(point, getScrollBarRectF())) {
				str = "scroll";
			}
			else if (pointFIsInRectF(point, getKeyboardButtonRectF())) {
				str = "keyboard";
			}
			
			// Check whether a button up.
			
			if (DBG) {
				Log.d(TAG, 
						"X" + i + ":" + x +
						",Y" + i + ":" + y +
						", id:" + id +
						"," + str);
			}
		}
		
		return true;
	}
	
	// Return the left button rectangle.
	private RectF getLeftButtonRectF() {
		RectF rectf = new RectF(
				0.0f,
				0.0f,
				mCanvasWidth * (0.5f - (SCROLLBAR_WIDTH_RATIO / 2.0f)),
				mCanvasHeight * BUTTON_HEIGHT_RATIO
		);
		
		return rectf;
	}
	
	// Return the right button rectangle.
	private RectF getRightButtonRectF() {
		RectF rectf = new RectF(
				mCanvasWidth * (0.5f - (SCROLLBAR_WIDTH_RATIO / 2.0f)),
				0.0f,
				mCanvasWidth * (1.0f - SCROLLBAR_WIDTH_RATIO),
				mCanvasHeight * BUTTON_HEIGHT_RATIO
		);
		
		return rectf;
	}
	
	// Return the Scroll rectangle.
	private RectF getScrollBarRectF() {
		RectF rectf = new RectF(
				mCanvasWidth * (1.0f - SCROLLBAR_WIDTH_RATIO),
				0.0f,
				mCanvasWidth,
				mCanvasHeight
		);
		
		return rectf;
	}
	
	// Return the keyboard button rectangle.
	private RectF getKeyboardButtonRectF() {
		RectF rectf = new RectF(
				0.0f,
				mCanvasHeight * (1.0f - KEYBOARD_BUTTON_HEIGHT_RATIO),
				mCanvasWidth * (1.0f - SCROLLBAR_WIDTH_RATIO),
				mCanvasHeight
		);
		
		return rectf;
	}
	
	// Return true if the point is in the rectangle.
	private static boolean pointFIsInRectF(PointF point, RectF rect) {
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
	}
}
