package jp.gr.java_conf.remota.android;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/** 
 * This is the main activity that displays the main menu. 
 */
public class Remota extends Activity {
	// Debugging
	private static final String TAG = "Remota";
	private static final boolean DBG = true;
	
	// Message types sent from the RemotaService handler
	/* package */ static final int MESSAGE_CONNECTION_STATE_CHANGE = 1;
	/* package */ static final int MESSAGE_READ = 2;
	/* package */ static final int MESSAGE_WRITE = 3;
	/* package */ static final int MESSAGE_DEVICE_NAME = 4;
	/* package */ static final int MESSAGE_TOAST = 5;
		
	// Intent request codes
	private static final int REQUEST_ENABLE_BLUETOOTH = 1;
	private static final int REQUEST_CONNECT_DEVICE   = 2;
	
	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";
	
	// Member fields
	private BluetoothAdapter mBluetoothAdapter = null; // Local bluetooth adapter
	private RemotaService mRemotaService = null;
	
	// The handler that gets information back from the RemotaService
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_CONNECTION_STATE_CHANGE:
				if (DBG) Log.i(TAG, "MESSAGE_CONNECTION_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case RemotaService.STATE_CONNECTED:
					// To full screen
					getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
					requestWindowFeature(Window.FEATURE_NO_TITLE);

					// Set up the window layout
					TouchPadView touchPadView = new TouchPadView(Remota.this, mRemotaService);
					setContentView(touchPadView);
					break;
				case RemotaService.STATE_CONNECTING:
					break;
				case RemotaService.STATE_LISTEN:
					break;
				case RemotaService.STATE_IDLE:
					break;
				}
				break;
			case MESSAGE_READ:
				if (DBG) Log.i(TAG, "Read message"); 
            	break;	
			}
		}
	};
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
    	if(DBG) Log.i(TAG, "+++ ON CREATE +++");
        
    	// Set up the window layout
    	setContentView(R.layout.main);
        
    	// Get the local bluetooth adapter
    	mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
    	// If Bluetooth is not supported, the adapter is null
    	if (mBluetoothAdapter == null) {
    		Toast.makeText(this, "Bluetooth is not supported.", Toast.LENGTH_LONG).show();
    		finish();
        	return;
    	} else {
    		mBluetoothAdapter.setName(getResources().getText(R.string.app_name).toString());
    	}

	}
    
	@Override
	public void onStart() {
		super.onStart();
    	
		if(DBG) Log.i(TAG, "+++ ON START +++");
    	
    	// If Bluetooth is not on, request it be enabled.
    	if (mBluetoothAdapter.isEnabled() == false) {
    		Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    		startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
    	}
    	else{
    		setup();
    	}
	}
    
	@Override
	public synchronized void onResume(){
		super.onResume();
    	
    	if(DBG) Log.i(TAG, "+++ ON RESUME +++");
    	
    	// Performing this check in onResume() covers the case in which BT was
    	// not enabled during onStart(), so we were paused to enable it...
    	// onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
    	if (mRemotaService != null) {
    		// Only if the state is STATE_NONE, do we know that we haven't started already
    		if (mRemotaService.getState() == RemotaService.STATE_IDLE) {
    			// Start the Bluetooth chat services
              mRemotaService.start();
    		}
        }
	}
    
	@Override
	public void onPause(){
		super.onPause();
    	
    	if (DBG) Log.i(TAG, "+++ ON PAUSE +++");
	}
    
	@Override
	public void onStop() {
		super.onStop();
    	
    	if (DBG) Log.i(TAG, "+++ ON STOP +++");
    }
    
	@Override
	public void onDestroy() {
		super.onDestroy();
    	
    	if (DBG) Log.i(TAG, "+++ ON DESTROY +++");
    	
    	// Stop the remota service
       if (mRemotaService != null) {
       	mRemotaService.stop();
        }
	}
    
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (DBG) Log.i(TAG, "+++ ON ACTIVITY RESULT +++ :" + resultCode);
    	
    	switch (requestCode) {
    	case REQUEST_ENABLE_BLUETOOTH:
    		// When the request to enable Bluetooth returns.
    		if (resultCode == Activity.RESULT_OK) {
    			// Bluetooth is enabled
    			setup();
    		}
    		break;
    	case REQUEST_CONNECT_DEVICE:
    		// When the request to connect the device returns.
    		if (resultCode == Activity.RESULT_OK) {
    			connectDevice(data);
    		}
    	}
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent serverIntent = null;
    	switch (item.getItemId()) {
    	case R.id.connect_device:
    		// Launch the DeviceListActivity to see devices and do scan
    		serverIntent = new Intent(this, RemotaDeviceListActivity.class);
    		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    		return true;
   		case R.id.make_discoverable:
   			// Ensure this device is discoverable by others
   			ensureDiscoverable();
   			return true;
   		case R.id.settings:
   			//
   			return true;
    	}
    	return false;
	}
   
	private void setup() {
		if (DBG) Log.i(TAG, "+++ SET UP +++");
    	
		if (mRemotaService == null) {
			mRemotaService = new RemotaService(this, mHandler);
		}
    }
    
	private void ensureDiscoverable(){
		if (DBG) Log.i(TAG, "ensure discoverable");
		if (mBluetoothAdapter.getScanMode() !=
			BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}
    
	private void connectDevice(Intent data) {
		// Get the device MAC address
		String address = data.getExtras().getString(RemotaDeviceListActivity.EXTRA_DEVICE_ADDRESS);
		// Get the BLuetoothDevice object
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		mRemotaService.connect(device);
	}
}
