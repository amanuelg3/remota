package jp.gr.java_conf.remota.android;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * This class does all the works for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class RemotaService {
	// Debugging
	private static final String TAG = "RemotaService";
	private static final boolean DBG = true;
    
	// Service name for the SDP
	private static final String SERVICE_NAME = "Serial Port";
    
	// Unique UUID for this application
	private static final UUID UUID_SPP =
		// 
		//UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
		// SPP
		UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    	// HID
    	//UUID.fromString("00001124-0000-1000-8000-00805F9B34FB");
    
	// Constants that indicate the current state
	public static final int STATE_IDLE = 0;       // now nothing to do
	public static final int STATE_LISTEN = 1;     // now listening for incoming connections
	public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
	public static final int STATE_CONNECTED = 3;  // now connected to a remote device
	
	// Class variables for Singleton
	private static RemotaService sRemotaService = null;
    
	// Member fields
	private final BluetoothAdapter mBluetoothAdapter;
	private Handler mHandler;
	private AcceptThread mAcceptThread;
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
	private int mState;
	
	/**
	 * 
	 * @param context
	 * @param handler
	 * @return
	 */
	public static RemotaService getInstance() {
		if (sRemotaService == null) {
			sRemotaService = new RemotaService();
		}
		
		return sRemotaService;
	}
	
	/**
	 * 
	 * @param handler
	 */
	public void setHandler(Handler handler) {
		mHandler = handler;
	}

	/**
     * Constructor
     * @param context
     * @param handler
     */
	private RemotaService() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mState = STATE_IDLE;
	}

    /**
     * Start the service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume() */
	public synchronized void start() {
		if (DBG) Log.d(TAG, "start");

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		
		// Cancel any thread attempting to make a connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
        	mConnectThread = null;
		}

		setState(STATE_LISTEN);

		// Start the thread to listen on a BluetoothServerSocket
		if (mAcceptThread == null) {
			mAcceptThread = new AcceptThread(true);
			mAcceptThread.start();
		}
	}
    
    /**
     * Set the current state of the chat connection
     * @param state  An integer defining the current connection state
     */
	private synchronized void setState(int state) {
		if (DBG) Log.d(TAG, "setState() " + mState + " -> " + state);
		mState = state;

		// Give the new state to the Handler so the UI Activity can update
		mHandler.obtainMessage(Remota.MESSAGE_CONNECTION_STATE_CHANGE, state, -1).sendToTarget();
	}

    /**
     * Return the current connection state. */
	public synchronized int getState() {
		return mState;
	}
    
    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * @param device  The BluetoothDevice to connect
     */
	public synchronized void connect(BluetoothDevice device) {
		if (DBG) Log.d(TAG, "connect to: " + device);

		// Cancel any thread attempting to make a connection
		if (mState == STATE_CONNECTING) {
			if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

		// Start the thread to connect with the given device
		mConnectThread = new ConnectThread(device);
		mConnectThread.start();
		setState(STATE_CONNECTING);
	}
    
    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
	public synchronized void connected(BluetoothSocket socket, BluetoothDevice device ) {
		if (DBG) Log.d(TAG, "connected" );

		// Cancel the thread that completed the connection
		if (mConnectThread != null) {
			//mConnectThread.cancel();
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
        	mConnectedThread = null;
		}

		// Cancel the accept thread because we only want to connect to one device
		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}

		// Start the thread to manage the connection and perform transmissions
		mConnectedThread = new ConnectedThread(socket);
		mConnectedThread.start();

		// Send the name of the connected device back to the UI Activity
		Message msg = mHandler.obtainMessage(Remota.MESSAGE_DEVICE_NAME);
		Bundle bundle = new Bundle();
		bundle.putString(Remota.DEVICE_NAME, device.getName());
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		setState(STATE_CONNECTED);
	}
    
    /**
     * Stop all threads
     */
	public synchronized void stop() {
		if (DBG) Log.d(TAG, "stop");
		
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}

		setState(STATE_IDLE);
	}
	
	/**
	 * Send a mouse event
	 * @param mouseEvent
	 */
	public void sendMouseEvent(MouseEvent mouseEvent) {
		if (mState == STATE_CONNECTED) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(bout);
			try{
				out.write(mouseEvent.getFlag());
				out.write(mouseEvent.getX());
				out.write(mouseEvent.getY());
				byte[] buffer = bout.toByteArray();
			
				mConnectedThread.write(buffer);
			} catch (IOException e) {
				Log.e(TAG, "SendMouseEvent:", e);
			}
		}
	}
    
    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
	private void connectionFailed() {
		// Send a failure message back to the Activity
		Message msg = mHandler.obtainMessage(Remota.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(Remota.TOAST, "Unable to connect device");
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		// Start the service over to restart listening mode
		RemotaService.this.start();
	}

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
    	// Send a failure message back to the Activity
    	Message msg = mHandler.obtainMessage(Remota.MESSAGE_TOAST);
    	Bundle bundle = new Bundle();
    	bundle.putString(Remota.TOAST, "Device connection was lost");
    	msg.setData(bundle);
    	mHandler.sendMessage(msg);
    	
    	// Start the service over to restart listening mode
    	RemotaService.this.start();
    }
    
    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
    	// The local server socket
    	private final BluetoothServerSocket mmServerSocket;

    	public AcceptThread(boolean secure) {
    		BluetoothServerSocket tmp = null;

    		// Create a new listening server socket
    		try {
    			tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(SERVICE_NAME, UUID_SPP);
    		} catch(IOException e) {
    			Log.e(TAG, "listen() failed", e);
    		}
    		mmServerSocket = tmp;
    	}

    	public void run() {
    		if (DBG) Log.d(TAG, "BEGIN mAcceptThread" + this);
    		setName("AcceptThread");

    		BluetoothSocket socket = null;

    		// Listen to the server socket if we're not connected
    		while (mState != STATE_CONNECTED) {
    			try {
    				// This is a blocking call and will only return on a
    				// successful connection or an exception
    				socket = mmServerSocket.accept();
    			} catch (IOException e) {
    				Log.e(TAG, "accept() failed", e);
    				break;
    			}

    			// If a connection was accepted
    			if (socket != null) {
    				synchronized (RemotaService.this) {
    					switch (mState) {
    					case STATE_LISTEN:
    					case STATE_CONNECTING:
    						// Situation normal. Start the connected thread.
    						connected(socket, socket.getRemoteDevice());
    						break;
    					case STATE_IDLE:
    					case STATE_CONNECTED:
    						// Either not ready or already connected. Terminate new socket.
    						try {
    							socket.close();
    						} catch (IOException e) {
    							Log.e(TAG, "Could not close unwanted socket", e);
    						}
    						break;
    					}
    				}
    			}
    		}
    		if (DBG) Log.i(TAG, "END mAcceptThread");
    	}

    	public void cancel() {
    		if (DBG) Log.d(TAG, "cancel " + this);
    		try {
    			mmServerSocket.close();
    		} catch(IOException e) {
    			Log.e(TAG, "close() of server failed", e);
    		}
    	}
    }


    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
    	private final BluetoothSocket mmSocket;
    	private final BluetoothDevice mmDevice;

    	public ConnectThread(BluetoothDevice device) {
    		mmDevice = device;
    		BluetoothSocket tmp = null;

    		// Get a BluetoothSocket for a connection with the
    		// given BluetoothDevice
    		try {
    			tmp = device.createRfcommSocketToServiceRecord(UUID_SPP);
    		} catch (IOException e) {
    			Log.e(TAG, "createRfcommSocketToServiceRecord() failed", e);
    		}
    		mmSocket = tmp;
        }

    	public void run() {
    		Log.i(TAG, "BEGIN mConnectThread");
    		setName("ConnectThread");

    		// Always cancel discovery because it will slow down a connection
    		mBluetoothAdapter.cancelDiscovery();

    		// Make a connection to the BluetoothSocket
    		try {
    			// This is a blocking call and will only return on a
    			// successful connection or an exception
    			mmSocket.connect();
    		} catch (IOException e) {
    			// Close the socket
    			try {
    				mmSocket.close();
    			} catch (IOException e2) {
    				Log.e(TAG, "unable to close() socket during connection failure", e2);
    			}
    			connectionFailed();
    			return;
    		}

    		// Reset the ConnectThread because we're done
    		synchronized (RemotaService.this) {
    			mConnectThread = null;
            }

    		// Start the connected thread
    		connected(mmSocket, mmDevice);
    	}

    	public void cancel() {
    		if (DBG) Log.d(TAG, "cancel " + this);    		
    		try {
    			mmSocket.close();
    		} catch (IOException e) {
    			Log.e(TAG, "close() of connect socket failed", e);
    		}
    	}
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
    	private final BluetoothSocket mmSocket;
    	private final InputStream mmInStream;
    	private final OutputStream mmOutStream;

    	public ConnectedThread(BluetoothSocket socket) {
    		Log.d(TAG, "create ConnectedThread: ");
    		mmSocket = socket;
        	InputStream tmpIn = null;
        	OutputStream tmpOut = null;

        	// Get the BluetoothSocket input and output streams
        	try {
        		tmpIn = socket.getInputStream();
        		tmpOut = socket.getOutputStream();
        	} catch (IOException e) {
        		Log.e(TAG, "temp sockets not created", e);
        	}

        	mmInStream = tmpIn;
        	mmOutStream = tmpOut;
    	}

    	public void run() {
    		Log.i(TAG, "BEGIN mConnectedThread");
        	byte[] buffer = new byte[1024];
        	int bytes;

        	// Keep listening to the InputStream while connected
        	while (true) {
        		try {
        			// Read from the InputStream
        			bytes = mmInStream.read(buffer);

        			// Send the obtained bytes to the UI Activity
        			mHandler.obtainMessage(Remota.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
        		} catch (IOException e) {
        			Log.e(TAG, "disconnected", e);
        			connectionLost();
        			break;
        		}
        	}
    	}

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
    	public void write(byte[] buffer) {
    		try {
    			mmOutStream.write(buffer);
        		
    			// Share the sent message back to the UI Activity
        		//mHandler.obtainMessage(Remota.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
    		} catch (IOException e) {
    			Log.e(TAG, "Exception during write", e);
    		}
    	}

    	public void cancel() {
    		if (DBG) Log.d(TAG, "cancel " + this);        	
        	try {
        		mmSocket.close();
        	} catch (IOException e) {
        		Log.e(TAG, "close() of connect socket failed", e);
        	}
    	}
    }
}
