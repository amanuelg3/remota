package jp.gr.java_conf.remota.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

/**
 * This Activity appears as a dialog. It lists any paired devices 
 * When a device is chosen by the user, the MAC address of the device 
 * is sent back to the parent Activity in the result Intent.
 */
public class TouchPadActivity extends Activity {
	// Debugging
	private static final String TAG = "TouchPadActivity";
	private static final boolean DBG = true;
	
	// Member fields
	private RemotaService mRemotaService = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(DBG) Log.i(TAG, "+++ ON CREATE +++");
		
		// To full screen
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// Set up the window layout
		TouchPadView touchPadView = new TouchPadView(this, mRemotaService);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(touchPadView);
		
		setResult(Activity.RESULT_OK);
	}
	
	@Override	protected void onDestroy() {
		super.onDestroy();
        
		if(DBG) Log.i(TAG, "+++ ON DESTROY +++");
	}
}
