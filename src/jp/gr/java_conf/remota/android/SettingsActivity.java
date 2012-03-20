/* Copyright (C) 2011-2012 Test Muroi (test.muroi@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 */

package jp.gr.java_conf.remota.android;

import java.util.List;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * 
 */
public class SettingsActivity extends PreferenceActivity {
	// Intent request codes
	private static final int REQUEST_PREVIEW_KEYBOARD = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preference);

		// Check whether gyro sensors is supported 
		SensorManager sm = (SensorManager)getSystemService(SENSOR_SERVICE);
		List<Sensor> list = sm.getSensorList(Sensor.TYPE_GYROSCOPE);
		if (list.isEmpty()) {
			ListPreference lp = (ListPreference)findPreference(getString(R.string.mode_key));
			lp.setEnabled(false);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu_on_settings, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
    	switch (item.getItemId()) {
    	case R.id.preview_keyboard_layout:
    		// Launch the KeyboardActivity to preview the keyboard layout.
    		intent = new Intent(this, KeyboardActivity.class);
    		startActivityForResult(intent, REQUEST_PREVIEW_KEYBOARD);
    		return true;
    	}
    	return false;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
    	case REQUEST_PREVIEW_KEYBOARD:
    		break;
		}
	}
}
