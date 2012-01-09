package jp.gr.java_conf.remota.android;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.Keyboard.Key;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class KeyboardActivity extends Activity implements KeyboardView.OnKeyboardActionListener {
	// Debugging
	private static final String TAG = "KeyboardActivity";
	private static final boolean DBG = false;
	
	// Member fields
	private Keyboard mKeyboard;
	private KeyboardView mKeyboardView;
	private List<Key> mStickyKeys;
	
	// The BroadcastReceiver that listens for disconnection
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
			if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
				if (DBG) Log.d(TAG, "disconnect!");
				KeyboardActivity.this.finish();
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (DBG) Log.i(TAG, "+++ ON CREATE +++");
		
		// Set up the KeyboardView.
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setGravity(Gravity.BOTTOM);
		linearLayout.setLayoutParams(
				new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT)
		);
		mKeyboard = new Keyboard(this, R.xml.qwerty);
		mKeyboardView = new KeyboardView(this, null);
		mKeyboardView.setKeyboard(mKeyboard);
		mKeyboardView.setOnKeyboardActionListener(this);
		linearLayout.addView(mKeyboardView);
		
		// Set up the list of the sticky keys
		mStickyKeys = new ArrayList<Key>();
		List<Key> keys = mKeyboard.getKeys();
		Key key = null;
		for (ListIterator<Key> it = keys.listIterator(); it.hasNext();) {
			key = it.next();
			if (key.sticky) {
				mStickyKeys.add(key);
			}
		}
		
		// To full screen
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		if (sp.getBoolean(getString(R.string.fullscreen_key), false)) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		
		// Set the screen orientation
		String orientation = sp.getString(getString(R.string.keyboard_orientation_key), getString(R.string.orientation_landscape_value));
		if (orientation.equals(getString(R.string.orientation_portrait_value))) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else if (orientation.equals(getString(R.string.orientation_landscape_value))) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else if (orientation.equals(getString(R.string.orientation_auto_value))) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		}
		
		//setContentView(mKeyboardView);
		setContentView(linearLayout);
		
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
	public void onPause(){
		// Send keyup events for the sticky keys
    	ArrayList<KeyboardEvent> keyboardEvents = new ArrayList<KeyboardEvent>();
    	RemotaService service = RemotaService.getInstance();
    	
    	if (addStickyKeyReleasedEvents(keyboardEvents) != 0) {
    		service.sendKeyboardEvent(keyboardEvents);
    	}
		
		super.onPause();
    	
    	if (DBG) Log.i(TAG, "+++ ON PAUSE +++");
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
		} else {
			// Check whether the pressed key is sticky and on
			Key key = null;
			int flag = KeyboardEvent.FLAG_KEYDOWN;
			for (ListIterator<Key> it = mStickyKeys.listIterator(); it.hasNext();) {
				key = it.next();
				if (key.codes[0] == primaryCode) {
					if (key.on) {
						flag = KeyboardEvent.FLAG_kEYUP;
					}
				}
			}
			
			KeyboardEvent keyboardEvent = new KeyboardEvent(
					flag,
					primaryCode
			);
			
			keyboardEvents.add(keyboardEvent);
			
			if (flag != KeyboardEvent.FLAG_kEYUP) {
				//addStickyKeyPressedEvents(keyboardEvents);
			}

			service.sendKeyboardEvent(keyboardEvents);
			
			state.setPressedScanCode(primaryCode);
		}
	}

	public void onRelease(int primaryCode) {
		if (DBG) Log.d(TAG, "release code:" + primaryCode);
		
		RemotaService service = RemotaService.getInstance();
		KeyboardState state = KeyboardState.getInstance();
		ArrayList<KeyboardEvent> keyboardEvents = new ArrayList<KeyboardEvent>();
		
		// Check whether the released key is sticky
		Key key = null;
		boolean releasedKeyIsSticky = false;
		for (ListIterator<Key> it = mStickyKeys.listIterator(); it.hasNext();) {
			key = it.next();
			
			if (key.codes[0] == primaryCode) {
				releasedKeyIsSticky = true;
			}
		}
		
		if (primaryCode == KeyboardEvent.SCANCODE_DONE) {
			// if the DONE key is released, do finish this activity.
			finish();
		} else if (releasedKeyIsSticky == false) {
			if	(state.getPressedScanCode() == KeyboardState.NOT_PRESSED) {
				// This case is a key repeat

				KeyboardEvent keyboardEvent = new KeyboardEvent(
						KeyboardEvent.FLAG_KEYDOWN,
						primaryCode
				);
				
				keyboardEvents.add(keyboardEvent);
				
				//addStickyKeyPressedEvents(keyboardEvents);
				
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
				// This case is not a key repeat 
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
	
	private int addStickyKeyPressedEvents(ArrayList<KeyboardEvent> keyboardEvents) {
		KeyboardEvent keyboardEvent = null;
		Key key = null;
		int counts = 0;
		for (ListIterator<Key> it = mStickyKeys.listIterator(); it.hasNext();) {
			key = it.next();
			
			if (key.on) {
				keyboardEvent = new KeyboardEvent(
						KeyboardEvent.FLAG_KEYDOWN,
						key.codes[0]
				);
				keyboardEvents.add(keyboardEvent);
				counts++;
			}
		}
		
		return counts;
	}
	
	private int addStickyKeyReleasedEvents(ArrayList<KeyboardEvent> keyboardEvents) {
		KeyboardEvent keyboardEvent = null;
		Key key = null;
		int counts = 0;
		for (ListIterator<Key> it = mStickyKeys.listIterator(); it.hasNext();) {
			key = it.next();
			
			if (key.on) {
				keyboardEvent = new KeyboardEvent(
						KeyboardEvent.FLAG_kEYUP,
						key.codes[0]
				);
				keyboardEvents.add(keyboardEvent);
				counts++;
			}
		}
		
		return counts;
	}
}
