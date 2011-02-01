package de.uni_kassel.android.lego.all;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import lejos.pc.comm.NXTCommBluecove;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public class Startup extends Activity {
	private NXTCommBluecove nxtCommBluecove = null;
	private Intent choiceDeviceIntent;
	private boolean connected = false;
	
	// Intent request codes
	private static final int REQUEST_CHOICE_DEVICE = 200;
	private static final int REQUEST_SETTINGS = 300;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_ENABLE_SCAN = 3;
    private static final int SCANTIME=10;

	private BroadcastReceiver mReceiverFound=null;
	private BroadcastReceiver mReceiverScanMode=null;
	private BluetoothAdapter mBluetoothAdapter=null;
	private Vector<BluetoothDevice> devices;
	private Timer resetScan = null;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Context context = this.getApplicationContext();
	}

	/*public void connection() {
		NXTCommBluecove connection = getConnection();
		if (connection != null) {
			if (connection.isOpened()) {
				//connection not open ie mmSocket==null
			} else {
				if (connection.initConnection()) {
					try {
						connection.search("", NXTCommFactory.BLUETOOTH);
					} catch (NXTCommException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	*/
	
	public boolean initConnection(){
		boolean result=true;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
		    // Device does not support Bluetooth
			return false;
		}
		if (!mBluetoothAdapter.isEnabled()) {
			result=false;
			//mBluetoothAdapter.enable();
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		return result;
	}
	
	public void connection() {
		//TODO finish method body
		if(connected){
			initConnection();
		}
	}
	
	public void scanBlueTooth(){
		// Create a BroadcastReceiver for ACTION_FOUND
		if(mReceiverFound==null){
			mReceiverFound = new BroadcastReceiver() {
				@Override
			    public void onReceive(Context context, Intent intent) {
			        String action = intent.getAction();
			        // When discovery finds a device
			        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
			            // Get the BluetoothDevice object from the Intent
			            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			            // Add the name and address to an array adapter to show in a ListView
			            devices.add(device);
			            //System.out.println("DEVICE: "+device.getName() + "\n" + device.getAddress());
			        }
			    }
			};
			// Register the BroadcastReceiver
			registerReceiver(mReceiverFound, new IntentFilter(BluetoothDevice.ACTION_FOUND));

			mReceiverScanMode = new BroadcastReceiver() {
				@Override
			    public void onReceive(Context context, Intent intent) {
			        String action = intent.getAction();
			        // When discovery finds a device
			        if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
			        	if(resetScan!=null){
			        		resetScan.cancel();
			        		resetScan=null;
			        	}
			        	Integer mode=(Integer) intent.getExtras().get(BluetoothAdapter.EXTRA_SCAN_MODE);
			        	//System.out.println(mode);
			        	if(mode==BluetoothAdapter.SCAN_MODE_CONNECTABLE){
			        		showDevices();
			        	}
			        }
			    }
			};
			registerReceiver(mReceiverScanMode, new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));
		}
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, SCANTIME);
		startActivityForResult(discoverableIntent, REQUEST_ENABLE_SCAN);
		resetScan = new Timer();
		resetScan.schedule(new TimerTask() {
			public void run() {
				if(devices.size()<1){
					showError("Fehler beim suchen");
				}else{
					showDevices();
				}
			}
		}, SCANTIME*2*1000);
		//this.owner.startActivity(discoverableIntent);
	}
	
	public void unregisterReceivers(){
		if(mReceiverFound!=null){
			unregisterReceiver(mReceiverFound);
		}
		if(mReceiverScanMode!=null){
			unregisterReceiver(mReceiverScanMode);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				// Get the device MAC address
				// String address =
				// data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				// Get the BLuetoothDevice object
				// BluetoothDevice device =
				// mBluetoothAdapter.getRemoteDevice(address);
				// Attempt to connect to the device
				// mChatService.connect(device);
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK && mBluetoothAdapter != null) {
				// Bluetooth is now enabled, so set up a chat session
				try {
					nxtCommBluecove = getConnection();
					nxtCommBluecove.search("", NXTCommFactory.BLUETOOTH);
				} catch (NXTCommException e) {
				}
			}
			break;
		case REQUEST_ENABLE_SCAN:
			if (resultCode == SCANTIME) {
				mBluetoothAdapter.startDiscovery();
			}
			break;

		case REQUEST_CHOICE_DEVICE:
			Bundle extras;
			if (data != null) {
				extras = data.getExtras();
			} else {
				extras = choiceDeviceIntent.getExtras();
			}
			if (extras != null) {
				String deviceKey = (String) extras
						.get(DeviceSelection.DEVICEKEY);
				String deviceMac = (String) extras
						.get(DeviceSelection.DEVICEMAC);
				openConnection(deviceKey, deviceMac);
			}
		}
	}

	private void openConnection(String deviceKey, String deviceMac) {
		boolean lConnect = true;
		if (deviceKey != null && deviceMac != null) {
			NXTInfo nxtInfo = new NXTInfo(NXTCommFactory.BLUETOOTH, deviceKey,
					deviceMac);
			try {
				getConnection().open(nxtInfo);

			} catch (NXTCommException e) {
				showError("Fehler bei der Verbindung: " + e.getMessage());
				lConnect = false;
			}
		}
		if (lConnect) {
			if (nxtCommBluecove.isOpened()) {
				/*
				 * TODO do something
				 */
			}
		}
	}

	public void showDevices() {

		for (BluetoothDevice device : devices) {
			if (device != null) {
				if (device.getName() != null) {
					System.out.println(device.getName());
				}
			}
		}
		ArrayList<String> keys = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();
		for (BluetoothDevice device : devices) {
			if (device != null) {
				if (device.getName() != null) {
					if (!keys.contains(device.getName())) {
						keys.add(device.getName());
						values.add(device.getAddress());
					}
				}
			}
		}
		if (keys.size() == 1) {
			openConnection(keys.get(0), values.get(0));
		} else {
			choiceDeviceIntent = new Intent().setClass(this,
					DeviceSelection.class);
			choiceDeviceIntent.putExtra(DeviceSelection.KEY, keys
					.toArray(new String[keys.size()]));
			choiceDeviceIntent.putExtra(DeviceSelection.VALUES, values
					.toArray(new String[values.size()]));
			this.startActivityForResult(choiceDeviceIntent,
					REQUEST_CHOICE_DEVICE);
		}
	}

	public void closeConnection() {
		if (nxtCommBluecove == null) {
			try {
				nxtCommBluecove.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public NXTCommBluecove getConnection() {
		if (nxtCommBluecove == null) {
			this.nxtCommBluecove = new NXTCommBluecove(mBluetoothAdapter);
		}
		return nxtCommBluecove;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// destroy objects
		unregisterReceivers();
	}

	public void showError(String text) {
		runOnUiThread(new showErrorRun(this, text));
	}

	private class showErrorRun implements Runnable {
		private String text = "";
		private Context owner;

		public showErrorRun(Context owner, String text) {
			this.text = text;
			this.owner = owner;
		}

		@Override
		public void run() {
			AlertDialog.Builder builder = new AlertDialog.Builder(owner);
			builder.setMessage(text);
			AlertDialog alert = builder.create();
			alert.show();
		}

	}
}