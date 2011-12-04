package jp.gr.java_conf.remota.android;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * 
 */
public class HelpActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		WebView webView = new WebView(this);
		String html = 
			"<html><head><body bgcolor=\"black\" text=\"white\">" + 
			getString(R.string.help_introduction) +
			getString(R.string.help_requirements) +
			getString(R.string.help_preparations) +
			getString(R.string.help_how_to_use) +
			"</body></head></html>";
		webView.loadData(html, "text/html", "UTF-8");
		
		setContentView(webView);
		
		setResult(Activity.RESULT_OK);
	}
}