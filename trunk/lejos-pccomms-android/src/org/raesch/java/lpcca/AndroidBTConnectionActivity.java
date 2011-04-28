package org.raesch.java.lpcca;

import java.util.HashMap;
import java.util.Vector;

import org.raesch.java.lpcca.R;
import org.raesch.java.lpcca.service.InterfaceLPCCARemoteService;
import org.raesch.java.lpcca.service.LPCCARemoteService;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class AndroidBTConnectionActivity extends Activity {

	public static final int REQUEST_CHOICE_DEVICE = 200;
	public static final int REQUEST_SETTINGS = 300;
	public static final int REQUEST_CONNECT_DEVICE = 1;
	public static final int REQUEST_ENABLE_BT = 2;
	public static final int REQUEST_ENABLE_SCAN = 3;
	public static final int SCANTIME = 10;
	public static final int CONNECTION_ESTABLISHED = 42;
	public static final int CONNECTION_FAILED = 43;
	public static final int REQUEST_INIT = 23;
	public static String KEY = "DEVICESKEY";
	public static String VALUES = "DEVICESVALUES";
	public static String DEVICEKEY = "SELECTEDDEVICEKEY";
	public static String DEVICEMAC = "SELECTEDDEVICEMAC";
	public static int SUCCESS = 1;
	public static int CANCEL = 2;
	public static String LOGTAG = "LPCCA AndroidBTConnectionActivity";
	private BroadcastReceiver bluetoothDeviceFoundBroadcastReceiver = null;
	private BroadcastReceiver bluetoothScanModeChangedBroadcastReceiver = null;
	private BluetoothAdapter mBluetoothAdapter = null;
	private HashMap<String, String> devices = new HashMap<String, String>();
	private String deviceKey;
	private String deviceMac;
	private Spinner deviceSpinner;
	private TextView textView;
	private Button connectBtn;
	private InterfaceLPCCARemoteService myRemoteService = null;
	private Vector<BluetoothDevice> bluetoothDevices = new Vector<BluetoothDevice>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(LOGTAG, "Activity AndroidBTConnection started.");
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
		Log.d(LOGTAG, "Setup called, initialising connections.");
		bindService(new Intent(LPCCARemoteService.class.getName()),
				serviceConnection, Context.BIND_AUTO_CREATE);
		initConnection();
	}

	public void restoreInstance(Bundle extras) {
		if (extras != null) {
			if (extras.containsKey(KEY)) {
				String[] deviceKeys = (String[]) extras.get(KEY);
				String[] deviceValues = (String[]) extras.get(VALUES);
				for (int z = 0; z < deviceKeys.length; z++) {
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
		this.setResult(CONNECTION_ESTABLISHED);
		try {
			myRemoteService.establishBTConnection(deviceKey, deviceMac);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.setResult(CONNECTION_FAILED);
		}
		finish();
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {
		// @Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
		}

		// @Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			myRemoteService = InterfaceLPCCARemoteService.Stub
					.asInterface(service);
		}
	};

	public void initConnection() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(LOGTAG, "No bluetooth adapter found.");
		} else {
			Log.d(LOGTAG, "Bluetooth adapter found.");
			if (!mBluetoothAdapter.isEnabled()) {
				Log.d(LOGTAG,
						"Bluetooth adapter not enabled, requesting enabling.");
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			} else {
				Log.d(LOGTAG, "BTAdapter found and enabled.");
				bluetoothEnabled();
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK && mBluetoothAdapter != null) {
				Log.d(LOGTAG, "Bluetooth enabled successfully.");
				bluetoothEnabled();
			} else {
				Log.e(LOGTAG, "Bluetooth enabling failed.");
			}
			break;
		case REQUEST_ENABLE_SCAN:
			if (resultCode == SCANTIME) {
				mBluetoothAdapter.startDiscovery();
			}
			break;
		}
	}

	private void bluetoothEnabled() {
		textView.setText("Bluetooth enabled, populating device list.");
		registerDeviceReceiver();
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
							bluetoothDevices.add(device);
							Log.d(LOGTAG,
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
		}
		Intent discoverableIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(
				BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
				SCANTIME);
		startActivityForResult(discoverableIntent,
				REQUEST_ENABLE_SCAN);
	}

	private void createDeviceList() {
		devices.clear();
		for (BluetoothDevice bluetoothDevice : bluetoothDevices) {
			devices.put(bluetoothDevice.getName(), bluetoothDevice.getAddress());
		}
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, devices.keySet().toArray(
						new String[devices.keySet().size()]));
		deviceSpinner.setAdapter(spinnerAdapter);
		textView.setText("Please select your NXT from the list below.");
		deviceSpinner.setVisibility(View.VISIBLE);
		connectBtn.setVisibility(View.VISIBLE);
	}

	// @Override
	public void onDestroy() {
		super.onDestroy();
		unbindService(serviceConnection);
		unregisterReceivers();
	}

	public void unregisterReceivers() {
		if (bluetoothDeviceFoundBroadcastReceiver != null) {
			unregisterReceiver(bluetoothDeviceFoundBroadcastReceiver);
		}
		if (bluetoothScanModeChangedBroadcastReceiver != null) {
			unregisterReceiver(bluetoothScanModeChangedBroadcastReceiver);
		}
	}
}
