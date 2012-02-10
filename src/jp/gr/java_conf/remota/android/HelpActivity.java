/* Copyright (C) 2011-2012 Test Muroi (test.muroi@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 */

package jp.gr.java_conf.remota.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * 
 */
public class HelpActivity extends Activity {
	// Keys for intent
	public static final String KEY_ANCHOR_LABEL = "KeyAnchorLabel";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		String anchorLabel = intent.getStringExtra(KEY_ANCHOR_LABEL);
		String helpUrl = getResources().getString(R.string.help_url);
		if (anchorLabel != null) {
			helpUrl += "#" + anchorLabel;
		}
		
		WebView webView = new WebView(this);
		WebSettings webSettings = webView.getSettings();
		webSettings.setBuiltInZoomControls(true);
		webSettings.setUseWideViewPort(true);
		webView.loadUrl(helpUrl);
		
		setContentView(webView);
		
		setResult(Activity.RESULT_OK);
	}
}