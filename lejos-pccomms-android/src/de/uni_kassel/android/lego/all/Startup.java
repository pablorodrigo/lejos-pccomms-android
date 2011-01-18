package de.uni_kassel.android.lego.all;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import lejos.pc.comm.NXTCommBluecove;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class Startup extends Activity {
	private NXTCommBluecove connection = null;
	private Intent choiceDeviceIntent;
	private int REQUEST_CHOICE_DEVICE = 200;
	private int REQUEST_SETTINGS = 300;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Context context = this.getApplicationContext();
		
	}

	public void connection() {
		NXTCommBluecove connection = getConnection();
		if (connection != null) {
			if (connection.isOpened()) {
				/*
				 * TODO do something
				 */
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
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CHOICE_DEVICE) {
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
		} else if (connection != null) {
			connection.onActivityResult(requestCode, resultCode, data);
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
			if (connection.isOpened()) {
				/*
				 * TODO do something
				 */
			}
		}
	}

	public void showDevices(Vector<BluetoothDevice> devices) {

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
		NXTCommBluecove connection = getConnection();
		try {
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public NXTCommBluecove getConnection() {
		if (connection == null) {
			this.connection = new NXTCommBluecove(this);
		}
		return connection;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// destroy objects
		if (connection != null) {
			connection.removeYou();
		}
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