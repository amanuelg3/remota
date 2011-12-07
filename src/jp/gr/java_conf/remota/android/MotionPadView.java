package jp.gr.java_conf.remota.android;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;

/** 
 * This is the SurfaceView like a gyro mouse.
 */
public class MotionPadView extends PadView {
	// Constants
	private static final float BUTTON_WIDTH_RATIO = 0.40f;
	private static final float SCROLLBAR_WIDTH_RATIO = 0.20f;
	private static final float SCROLLBAR_HEIGHT_RATIO = 0.30f;
	private static final float KEYBOARD_BUTTON_HEIGHT_RATIO = 0.10f;
	
	/**
     * Constructor
     * @param context
     * @param remotaService
     */
	public MotionPadView(Context context) {
		super(context);
	}
	
	// Return the left button rectangle.
	@Override
	public RectF getLeftButtonRectF() {
		RectF rectf = new RectF(
				0.0f,
				0.0f,
				mCanvasWidth * BUTTON_WIDTH_RATIO,
				mCanvasHeight * (1.0f - KEYBOARD_BUTTON_HEIGHT_RATIO)
		);
		
		return rectf;
	}
	
	// Return the right button rectangle.
	@Override
	public RectF getRightButtonRectF() {
		RectF rectf = new RectF(
				mCanvasWidth * (BUTTON_WIDTH_RATIO + SCROLLBAR_WIDTH_RATIO),
				0.0f,
				mCanvasWidth,
				mCanvasHeight * (1.0f - KEYBOARD_BUTTON_HEIGHT_RATIO)
		);
		
		return rectf;
	}
	
	// Return the Scroll rectangle.
	@Override
	public RectF getScrollBarRectF() {
		RectF rectf = new RectF(
				mCanvasWidth * BUTTON_WIDTH_RATIO,
				mCanvasHeight * (1.0f - KEYBOARD_BUTTON_HEIGHT_RATIO - SCROLLBAR_HEIGHT_RATIO),
				mCanvasWidth * (BUTTON_WIDTH_RATIO + SCROLLBAR_WIDTH_RATIO),
				mCanvasHeight * (1.0f - KEYBOARD_BUTTON_HEIGHT_RATIO)
		);
		
		return rectf;
	}
	
	// Return the keyboard button rectangle.
	@Override
	public RectF getKeyboardButtonRectF() {
		RectF rectf = new RectF(
				0.0f,
				mCanvasHeight * (1.0f - KEYBOARD_BUTTON_HEIGHT_RATIO),
				mCanvasWidth,
				mCanvasHeight
		);
		
		return rectf;
	}
	
	// Return the touch pad rectangle.
	@Override
	public RectF getMovePadRectF() {
		RectF rectf = new RectF(
				mCanvasWidth * BUTTON_WIDTH_RATIO,
				0,
				mCanvasWidth * (BUTTON_WIDTH_RATIO + SCROLLBAR_WIDTH_RATIO),
				mCanvasHeight * (1.0f - KEYBOARD_BUTTON_HEIGHT_RATIO - SCROLLBAR_HEIGHT_RATIO)
		);
		
		return rectf;
	}
	
	// Return the left button paint
	@Override
	public Paint getLeftButtonPaint() {
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.FILL);
		RectF rectf = getLeftButtonRectF();
		LinearGradient shader;
		if (mTouchState.getLeftButtonState() == TouchState.NOT_PRESSED) {
			shader = new LinearGradient(
					rectf.left, rectf.top, 
					rectf.left, rectf.bottom,
					getResources().getColor(R.color.button0_m),
					getResources().getColor(R.color.button1_m),
					TileMode.REPEAT
			);
		} else {
			shader = new LinearGradient(
					rectf.left, rectf.top, 
					rectf.left, rectf.bottom,
					getResources().getColor(R.color.button1_m),
					getResources().getColor(R.color.button0_m),
					TileMode.REPEAT
			);
		}
		paint.setShader(shader);
		
		return paint;
	}
	
	// Return the right button paint
	@Override
	public Paint getRightButtonPaint() {
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.FILL);
		RectF rectf = getRightButtonRectF();
		LinearGradient shader;
		if (mTouchState.getRightButtonState() == TouchState.NOT_PRESSED) {
			shader = new LinearGradient(
					rectf.left, rectf.top, 
					rectf.left, rectf.bottom,
					getResources().getColor(R.color.button0_m),
					getResources().getColor(R.color.button1_m),
					TileMode.REPEAT
			);
		} else {
			shader = new LinearGradient(
					rectf.left, rectf.top, 
					rectf.left, rectf.bottom,
					getResources().getColor(R.color.button1_m), 
					getResources().getColor(R.color.button0_m),
					TileMode.REPEAT
			);
		}
		paint.setShader(shader);
		
		return paint;
	}
	
	// Return the scroll button paint
	@Override
	public Paint getScrollBarPaint() {
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.FILL);
		RectF rectf = getScrollBarRectF();
		LinearGradient shader;
		if (mTouchState.getScrollBarState() == TouchState.NOT_PRESSED) {
			shader = new LinearGradient(
					rectf.left, rectf.top, 
					rectf.left, rectf.bottom / 2.0f,
					getResources().getColor(R.color.button1_m),
					getResources().getColor(R.color.button0_m),
					TileMode.MIRROR
			);
		} else {
			shader = new LinearGradient(
					rectf.left, rectf.top, 
					rectf.left, rectf.bottom / 2.0f,
					getResources().getColor(R.color.button0_m), 
					getResources().getColor(R.color.button1_m),
					TileMode.MIRROR
			);
		}
		paint.setShader(shader);
		
		return paint;
	}
	
	// Return the keyboard button paint
	@Override
	public Paint getKeyboardButtonPaint() {
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.FILL);
		RectF rectf = getKeyboardButtonRectF();
		LinearGradient shader;
		if (mTouchState.getKeyboardButtonState() == TouchState.NOT_PRESSED) {
			shader = new LinearGradient(
					rectf.left, rectf.top, 
					rectf.left, rectf.bottom,
					getResources().getColor(R.color.button0_m),
					getResources().getColor(R.color.button1_m),
					TileMode.REPEAT
			);
		} else {
			shader = new LinearGradient(
					rectf.left, rectf.top, 
					rectf.left, rectf.bottom,
					getResources().getColor(R.color.button1_m),
					getResources().getColor(R.color.button0_m),
					TileMode.REPEAT
			);
		}
		paint.setShader(shader);
		
		return paint;
	}
	
	// Return the touch pad paint
	@Override
	public Paint getMovePadPaint() {
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.FILL);
		RectF rectf = getMovePadRectF();
		LinearGradient shader;
		if (mTouchState.getMovePadState() == TouchState.NOT_PRESSED) {
			shader = new LinearGradient(
					rectf.left, rectf.top, 
					rectf.left, rectf.bottom / 2.0f,
					getResources().getColor(R.color.button1_m),
					getResources().getColor(R.color.button0_m),
					TileMode.MIRROR
			);
		} else {
			shader = new LinearGradient(
					rectf.left, rectf.top, 
					rectf.left, rectf.bottom / 2.0f,
					getResources().getColor(R.color.button0_m), 
					getResources().getColor(R.color.button1_m),
					TileMode.MIRROR
			);
		}
		paint.setShader(shader);
		
		return paint;
	}
	
	// Return the stroke paint
	@Override
	public Paint getStrokePaint() {
		Paint paint = new Paint();
		paint.setColor(getResources().getColor(R.color.stroke));
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(2.0f);
		
		return paint;
	}
}
