package jp.gr.java_conf.remota.android;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * 
 */
public class HelpActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		WebView webView = new WebView(this);
		WebSettings webSettings = webView.getSettings();
		webSettings.setBuiltInZoomControls(true);
		webSettings.setUseWideViewPort(true);
		webView.loadUrl(getResources().getString(R.string.help_url));
		
		setContentView(webView);
		
		setResult(Activity.RESULT_OK);
	}
}