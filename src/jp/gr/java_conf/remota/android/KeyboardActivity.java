/* Copyright (C) 2011-2012 Test Muroi (test.muroi@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 */

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
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class KeyboardActivity extends Activity implements KeyboardView.OnKeyboardActionListener {
	// Debugging
	private static final String TAG = "KeyboardActivity";
	private static final boolean DBG = false;
	
	private static final int NUM_KEYBOARD_ROWS = 7;
	
	// Member fields
	private Keyboard mKeyboard;
	private KeyboardView mKeyboardView;
	private List<Key> mStickyKeys;
	private boolean mUpdated = false;
	
	private Handler mHandler = new Handler();
	
	private Runnable mUpdateKeyboardTask = new Runnable() {
		public void run() {
			if (mUpdated == false) { 
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(KeyboardActivity.this);
				// Get the keyboard key witdh dip
				String keyHeightSetting = sp.getString(
						getString(R.string.keyboard_key_height_key), 
						getString(R.string.pref_keyboard_key_height_default_value)
				);
				int keyHeightDip;
				try {
					keyHeightDip = Integer.parseInt(keyHeightSetting);
				} catch (NumberFormatException e) {
					keyHeightDip = 0;
				}
			
				// Calculate the key height value
				DisplayMetrics dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);
				float density = dm.density;
				float keyHeight;
				if (keyHeightDip <= 0) {
					keyHeight = mKeyboard.getHeight() / NUM_KEYBOARD_ROWS;
				} else {
					// keyHeightDip > 0
					keyHeight = keyHeightDip * density;
				}

				// Set up the list of the sticky keys and key width
				mStickyKeys = new ArrayList<Key>();
				List<Key> keys = mKeyboard.getKeys();
				Key key = null;
				int rowIndex = 0;
				for (ListIterator<Key> it = keys.listIterator(); it.hasNext();) {
					key = it.next();
					rowIndex = (int)(key.y / key.height);
					key.height = (int)keyHeight;
					key.y = (int)(keyHeight * (-NUM_KEYBOARD_ROWS + rowIndex) + mKeyboard.getHeight());
					if (key.sticky) {
						mStickyKeys.add(key);
					}
				}
				
				mUpdated = true;
			}
		}
	};
	
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
		
		// To full screen
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		//if (sp.getBoolean(getString(R.string.fullscreen_key), false)) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//}

		// Hide the title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// Set the screen orientation
		String orientation = sp.getString(getString(R.string.keyboard_orientation_key), getString(R.string.orientation_landscape_value));
		if (orientation.equals(getString(R.string.orientation_portrait_value))) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else if (orientation.equals(getString(R.string.orientation_landscape_value))) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else if (orientation.equals(getString(R.string.orientation_auto_value))) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		}
		
		// Set the keyboard
		mKeyboard = new Keyboard(this, R.xml.qwerty);
		mKeyboardView = new KeyboardView(this, null);
		mKeyboardView.setKeyboard(mKeyboard);
		mKeyboardView.setOnKeyboardActionListener(this);
		linearLayout.addView(mKeyboardView);
		
		// Update the keyboard information
		mHandler.post(mUpdateKeyboardTask);
		
		//setContentView(mKeyboardView);
		setContentView(linearLayout);
		
		// Register for broadcasts when device is disconnected
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		registerReceiver(mReceiver, filter);

		setResult(Activity.RESULT_OK);
	}
	
	@Override
	public synchronized void onResume(){
		super.onResume();
		
		if(DBG) Log.i(TAG, "+++ ON RESUME +++");
		
		// Update the keyboard information
		mHandler.post(mUpdateKeyboardTask);
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
