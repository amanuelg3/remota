/* Copyright (C) 2011-2012 Test Muroi (test.muroi@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 */

package jp.gr.java_conf.remota.android;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

/**
 * 
 */
public class InformationActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.information);
		setResult(Activity.RESULT_OK);
		
		TextView versionTextView = (TextView)findViewById(R.id.textViewVersion);
		PackageManager pm = getPackageManager();
		try {
			PackageInfo info = null;
			info = pm.getPackageInfo("jp.gr.java_conf.remota.android", 0);
			versionTextView.setText("Version: " + info.versionName);
		} catch (NameNotFoundException ex) {
			versionTextView.setText("Version: unknown");
		}
	}
}