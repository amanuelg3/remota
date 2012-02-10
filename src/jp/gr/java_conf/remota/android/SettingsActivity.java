/* Copyright (C) 2011-2012 Test Muroi (test.muroi@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 */

package jp.gr.java_conf.remota.android;

import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

/**
 * 
 */
public class SettingsActivity extends PreferenceActivity {
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
}
