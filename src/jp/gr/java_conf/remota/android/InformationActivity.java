package jp.gr.java_conf.remota.android;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
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