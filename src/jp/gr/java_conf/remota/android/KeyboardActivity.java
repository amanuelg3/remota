package jp.gr.java_conf.remota.android;

import java.util.ArrayList;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.util.Log;

public class KeyboardActivity extends Activity implements KeyboardView.OnKeyboardActionListener {
	// Debugging
	private static final String TAG = "KeyboardActivity";
	private static final boolean DBG = true;

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
		if (DBG) Log.d(TAG, "+++ ON KEY +++");
	}
	
	public void onPress(int primaryCode) {
		if (DBG) Log.d(TAG, "press code:" + primaryCode);
		
		RemotaService service = RemotaService.getInstance();
		KeyboardState state = KeyboardState.getInstance();
		ArrayList<KeyboardEvent> keyboardEvents = new ArrayList<KeyboardEvent>();
		
		if (primaryCode == KeyboardEvent.SCANCODE_DONE) {
			// if the DONE key is pressed, do nothing.
		} else if (
				primaryCode == KeyboardEvent.SCANCODE_SHIFT ||
				primaryCode == KeyboardEvent.SCANCODE_CTRL ||
				primaryCode == KeyboardEvent.SCANCODE_ALT
		) {
			int flag;
			
			if (
					(state.getAltPressed() && primaryCode == KeyboardEvent.SCANCODE_ALT) ||
					(state.getCtrlPressed() && primaryCode == KeyboardEvent.SCANCODE_CTRL) ||
					(state.getShiftPressed() && primaryCode == KeyboardEvent.SCANCODE_SHIFT)
			) {
				flag = KeyboardEvent.FLAG_kEYUP;
			} else {
				flag = KeyboardEvent.FLAG_KEYDOWN;
			}
			KeyboardEvent keyboardEvent = new KeyboardEvent(flag, primaryCode);
		
			keyboardEvents.add(keyboardEvent);
			
			service.sendKeyboardEvent(keyboardEvents);
			
			if (primaryCode == KeyboardEvent.SCANCODE_SHIFT) {
				if (state.getShiftPressed()) {
					state.setShiftPressed(false);
				} else {
					state.setShiftPressed(true);
				}
			} else if (primaryCode == KeyboardEvent.SCANCODE_CTRL) {
				if (state.getCtrlPressed()) {
					state.setCtrlPressed(false);
				} else {
					state.setCtrlPressed(true);
				}
			} else if (primaryCode == KeyboardEvent.SCANCODE_ALT) {
				if (state.getAltPressed()) {
					state.setAltPressed(false);
				} else {
					state.setAltPressed(true);
				}
			}
		} else {
			KeyboardEvent keyboardEvent = new KeyboardEvent(
					KeyboardEvent.FLAG_KEYDOWN,
					primaryCode
			);
			
			keyboardEvents.add(keyboardEvent);
			
			addStickyKeyPressedEvents(keyboardEvents);

			service.sendKeyboardEvent(keyboardEvents);
			
			state.setPressedScanCode(primaryCode);
		}
	}

	public void onRelease(int primaryCode) {
		if (DBG) Log.d(TAG, "release code:" + primaryCode);
		
		RemotaService service = RemotaService.getInstance();
		KeyboardState state = KeyboardState.getInstance();
		ArrayList<KeyboardEvent> keyboardEvents = new ArrayList<KeyboardEvent>();
		
		if (primaryCode == KeyboardEvent.SCANCODE_DONE) {
			// if the DONE key is released, do finish this activity.
			finish();
		} else if (
				primaryCode != KeyboardEvent.SCANCODE_ALT &&
				primaryCode != KeyboardEvent.SCANCODE_CTRL && 
				primaryCode != KeyboardEvent.SCANCODE_SHIFT
		) {
			if	(state.getPressedScanCode() == KeyboardState.NOT_PRESSED) {
				// This case is a key repeat
			
				KeyboardEvent keyboardEvent = new KeyboardEvent(
						KeyboardEvent.FLAG_KEYDOWN,
						primaryCode
				);
			
				keyboardEvents.add(keyboardEvent);
			
				addStickyKeyPressedEvents(keyboardEvents);
				
				service.sendKeyboardEvent(keyboardEvents);
			
				keyboardEvent = new KeyboardEvent(
						KeyboardEvent.FLAG_kEYUP,
						primaryCode
				);
			
				keyboardEvents.clear();
				keyboardEvents.add(keyboardEvent);
		
				service.sendKeyboardEvent(keyboardEvents);
			
				state.setPressedScanCode(KeyboardState.NOT_PRESSED);
			} else {
				KeyboardEvent keyboardEvent = new KeyboardEvent(
						KeyboardEvent.FLAG_kEYUP,
						primaryCode
				);
			
				keyboardEvents.add(keyboardEvent);
		
				service.sendKeyboardEvent(keyboardEvents);
			
				state.setPressedScanCode(KeyboardState.NOT_PRESSED);
			}
		}
	}
	
	public void onText(CharSequence text) {
		if (DBG) Log.d(TAG, "+++ ON TEXT +++");
	}
	
	public void swipeDown() {
		if (DBG) Log.d(TAG, "+++ ON SWIPE DOWN +++");
	}
	
	public void swipeUp() {
		if (DBG) Log.d(TAG, "+++ ON SWIPE UP +++");
	}
	
	public void swipeLeft() {
		if (DBG) Log.d(TAG, "+++ ON SWIPE LEFT +++");
	}
	
	public void swipeRight() {
		if (DBG) Log.d(TAG, "+++ ON SWIPE RIGHT +++");
	}
	
	private void addStickyKeyPressedEvents(ArrayList<KeyboardEvent> keyboardEvents) {
		KeyboardState state = KeyboardState.getInstance();
		KeyboardEvent keyboardEvent = null;
		
		if (state.getAltPressed()) {
			keyboardEvent = new KeyboardEvent(
					KeyboardEvent.FLAG_KEYDOWN,
					KeyboardEvent.SCANCODE_ALT
			);
			keyboardEvents.add(keyboardEvent);
		}
		
		if (state.getCtrlPressed()) {
			keyboardEvent = new KeyboardEvent(
					KeyboardEvent.FLAG_KEYDOWN,
					KeyboardEvent.SCANCODE_CTRL
			);
			keyboardEvents.add(keyboardEvent);
		}
		
		if (state.getShiftPressed()) {
			keyboardEvent = new KeyboardEvent(
					KeyboardEvent.FLAG_KEYDOWN,
					KeyboardEvent.SCANCODE_SHIFT
			);
			keyboardEvents.add(keyboardEvent);
		}
	}
}
