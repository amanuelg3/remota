package jp.gr.java_conf.remota.android;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.util.Log;

public class KeyboardActivity extends Activity implements KeyboardView.OnKeyboardActionListener {
	// Debugging
	private static final String TAG = "KeyboardActivity";
	private static final boolean DBG = true;
	
	// Constants
	private static final int DONE = -3;
	
	// Member fields
	private Keyboard mKeyboard;
	private KeyboardView mKeyboardView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (DBG) Log.i(TAG, "+++ ON CREATE +++");
		
		// Set up the KeyboardView. 
		mKeyboard = new Keyboard(this, R.xml.qwerty);
		mKeyboardView = new KeyboardView(this, null);
		mKeyboardView.setKeyboard(mKeyboard);
		mKeyboardView.setOnKeyboardActionListener(this);
		
		setContentView(mKeyboardView);

		setResult(Activity.RESULT_OK);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
        
		if(DBG) Log.i(TAG, "+++ ON DESTROY +++");
	}
	
	public void onKey(int primaryCode, int[] keyCodes) {
		
	}
	
	public void onPress(int primaryCode) {
		if (DBG) Log.d(TAG, "press code:" + primaryCode);
		
		if (primaryCode == DONE) {
			finish();
		}
		
		RemotaService service = RemotaService.getInstance();
		
		KeyboardEvent keyboardEvent = new KeyboardEvent(
				KeyboardEvent.FLAG_KEYDOWN,
				primaryCode
		);
		
		service.sendKeyboardEvent(keyboardEvent);
	}

	public void onRelease(int primaryCode) {
		if (DBG) Log.d(TAG, "release code:" + primaryCode);
		
		RemotaService service = RemotaService.getInstance();
		
		KeyboardEvent keyboardEvent = new KeyboardEvent(
				KeyboardEvent.FLAG_kEYUP,
				primaryCode
		);
		
		service.sendKeyboardEvent(keyboardEvent);
	}
	
	public void onText(CharSequence text) {
		
	}
	
	public void swipeDown() {
		
	}
	
	public void swipeUp() {
		
	}
	
	public void swipeLeft() {
		
	}
	
	public void swipeRight() {
		
	}
}
