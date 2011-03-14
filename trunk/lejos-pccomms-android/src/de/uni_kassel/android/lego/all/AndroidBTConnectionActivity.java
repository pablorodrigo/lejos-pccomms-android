package de.uni_kassel.android.lego.all;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class AndroidBTConnectionActivity extends Activity {

	public static final int CONNECTION_ESTABLISHED = 42;
	public static final int REQUEST_INIT = 23;
	public static String KEY = "DEVICESKEY";
	public static String VALUES = "DEVICESVALUES";
	public static String DEVICEKEY = "SELECTEDDEVICEKEY";
	public static String DEVICEMAC = "SELECTEDDEVICEMAC";
	public static int SUCCESS = 1;
	public static int CANCEL = 2;

	private BroadcastReceiver bluetoothDeviceFoundBroadcastReceiver = null;
	private BroadcastReceiver bluetoothScanModeChangedBroadcastReceiver = null;
	private BluetoothAdapter mBluetoothAdapter = null;
	private Timer resetScan = null;
	private HashMap<String, String> devices = new HashMap<String, String>();
	private String deviceKey;
	private String deviceMac;
	private NXTCommBluecove nxtCommBluecove = null;
	private Spinner deviceSpinner;
	private TextView textView;
	private Button connectBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(DataManager.LOG_TAG, "Activity AndroidBTConnection started.");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deviceselection);
		connectBtn = (Button) findViewById(R.id.ConnectBtn);
		textView = (TextView) findViewById(R.id.TextView01);
		deviceSpinner = (Spinner) findViewById(R.id.Devices);

		connectBtn.setVisibility(View.INVISIBLE);
		deviceSpinner.setVisibility(View.INVISIBLE);

		textView.setText("Requesting bluetooth and populating device list.");

		restoreInstance(this.getIntent().getExtras());
		connectBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onDeviceSelectClick();
			}
		});

		setup();
	}

	private void setup() {
		Log.d(DataManager.LOG_TAG, "Setup called, initialising connections.");
		initConnection();
	}

	public void restoreInstance(Bundle extras) {
		if (extras != null) {
			if (extras.containsKey(KEY)) {
				String[] deviceKeys = (String[]) extras.get(KEY);
				String[] deviceValues = (String[]) extras.get(VALUES);
				for (int z = 0; z < deviceKeys.length; z++) {
					// String[] value=deviceString[z].split(",");
					devices.put(deviceKeys[z], deviceValues[z]);
				}
				ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
						this, android.R.layout.simple_spinner_item, devices
								.keySet().toArray(
										new String[devices.keySet().size()]));
				deviceSpinner.setAdapter(spinnerAdapter);
			}
		}
	}

	public void onDeviceSelectClick() {
		this.deviceKey = deviceSpinner.getSelectedItem().toString();
		this.deviceMac = devices.get(deviceKey);
		// TODO
		if (deviceKey != null && deviceMac != null) {
			NXTInfo nxtInfo = new NXTInfo(NXTCommFactory.BLUETOOTH, deviceKey,
					deviceMac);
			try {
				nxtCommBluecove.open(nxtInfo);

			} catch (NXTCommException e) {
				showError("Fehler bei der Verbindung: " + e.getMessage());
			}
		}
		this.setResult(CONNECTION_ESTABLISHED);
		finish();
	}

	public void initConnection() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(DataManager.LOG_TAG, "No bluetooth adapter found.");
		} else {
			Log.d(DataManager.LOG_TAG, "Bluetooth adapter found.");
			nxtCommBluecove = new NXTCommBluecove(mBluetoothAdapter);
			if (!mBluetoothAdapter.isEnabled()) {
				Log.d(DataManager.LOG_TAG,
						"Bluetooth adapter not enabled, requesting enabling.");
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent,
						DataManager.REQUEST_ENABLE_BT);
			} else {
				Log.d(DataManager.LOG_TAG, "BTAdapter found and enabled.");
				bluetoothEnabled();
			}
		}
	}

	public void unregisterReceivers() {
		if (bluetoothDeviceFoundBroadcastReceiver != null) {
			unregisterReceiver(bluetoothDeviceFoundBroadcastReceiver);
		}
		if (bluetoothScanModeChangedBroadcastReceiver != null) {
			unregisterReceiver(bluetoothScanModeChangedBroadcastReceiver);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case DataManager.REQUEST_CONNECT_DEVICE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				// Get the device MAC address
				// String address =
				// data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				// Get the BLuetoothDevice object
				// BluetoothDevice device =
			}
			break;

		case DataManager.REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK && mBluetoothAdapter != null) {
				Log.d(DataManager.LOG_TAG, "Bluetooth enabled successfully.");
				bluetoothEnabled();
			} else {
				Log.e(DataManager.LOG_TAG, "Bluetooth enabling failed.");
			}
			break;

		case DataManager.REQUEST_ENABLE_SCAN:
			if (resultCode == DataManager.SCANTIME) {
				mBluetoothAdapter.startDiscovery();
			}
			break;
		}
	}

	private void bluetoothEnabled() {
		textView.setText("Bluetooth enabled, populating device list.");
		/*
		 * trying to avoid nxtcommbluecove // Bluetooth is now enabled, so set
		 * up a chat session try { this.nxtCommBluecove = new NXTCommBluecove(
		 * mBluetoothAdapter); nxtCommBluecove.search("",
		 * NXTCommFactory.BLUETOOTH); } catch (NXTCommException e) {
		 * Log.e(DataManager.LOG_TAG,
		 * "Unable to get connection from NXTCommBluecove"); }
		 */
		registerDeviceReceiver();
		// if (!mBluetoothAdapter.isDiscovering()) {
		// }
		// createDeviceList();
	}

	public void registerDeviceReceiver() {
		if (bluetoothDeviceFoundBroadcastReceiver == null) {
			// Create a BroadcastReceiver for ACTION_FOUND
			bluetoothDeviceFoundBroadcastReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					String action = intent.getAction();
					// When discovery finds a device
					if (BluetoothDevice.ACTION_FOUND.equals(action)) {
						// Get the BluetoothDevice object from the Intent
						BluetoothDevice device = intent
								.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
						// Add the name and address to an array adapter to show
						// in a ListView
						if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
							DataManager.getInstance().getBluetoothDevices()
									.add(device);
							Log.d(DataManager.LOG_TAG,
									"Added discovered & paired device: "
											+ device.getName() + ", "
											+ device.getAddress());
							createDeviceList();
						}
					}
				}
			};

			// Register the BroadcastReceiver
			registerReceiver(bluetoothDeviceFoundBroadcastReceiver,
					new IntentFilter(BluetoothDevice.ACTION_FOUND));

			/*
			 * not sure if we really want this // Create a BroadcastReceiver for
			 * ACTION_SCAN_MODE_CHANGED
			 * bluetoothScanModeChangedBroadcastReceiver = new
			 * BroadcastReceiver() {
			 * 
			 * @Override public void onReceive(Context context, Intent intent) {
			 * String action = intent.getAction(); // When discovery finds a
			 * device if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED
			 * .equals(action)) { if (resetScan != null) { resetScan.cancel();
			 * resetScan = null; } Integer mode = (Integer)
			 * intent.getExtras().get( BluetoothAdapter.EXTRA_SCAN_MODE); //
			 * System.out.println(mode); if (mode ==
			 * BluetoothAdapter.SCAN_MODE_CONNECTABLE) { showDevices(); } } } };
			 * 
			 * // Register the BroadcastReceiver
			 * registerReceiver(bluetoothScanModeChangedBroadcastReceiver, new
			 * IntentFilter( BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));
			 */
		}

		Intent discoverableIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(
				BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
				DataManager.SCANTIME);
		startActivityForResult(discoverableIntent,
				DataManager.REQUEST_ENABLE_SCAN);

		/*
		 * resetScan = new Timer(); resetScan.schedule(new TimerTask() { public
		 * void run() { if
		 * (DataManager.getInstance().getBluetoothDevices().size() < 1) {
		 * showError("No device found."); } else { createDeviceList(); } } },
		 * DataManager.SCANTIME * 2 * 1000);
		 */
		// startActivity(discoverableIntent);
	}

	private void createDeviceList() {
		devices.clear();
		for (BluetoothDevice bluetoothDevice : DataManager.getInstance()
				.getBluetoothDevices()) {
			devices
					.put(bluetoothDevice.getName(), bluetoothDevice
							.getAddress());
		}
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, devices.keySet().toArray(
						new String[devices.keySet().size()]));
		deviceSpinner.setAdapter(spinnerAdapter);
		textView.setText("Please select your NXT from the list below.");
		deviceSpinner.setVisibility(View.VISIBLE);
		connectBtn.setVisibility(View.VISIBLE);

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
