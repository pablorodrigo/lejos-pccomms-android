package de.uni_kassel.android.lego.all;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.addon.MSC;
import lejos.pc.comm.NXTCommBluecove;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class Startup extends Activity {
	private NXTCommBluecove connection = null;
	private Intent choiceDeviceIntent;
	private int REQUEST_CHOICE_DEVICE = 200;
	private int REQUEST_SETTINGS = 300;
	private BluetoothAdapter mBluetoothAdapter=null;
	// Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_ENABLE_SCAN = 3;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	}

	public void connection() {
		NXTCommBluecove connection = getConnection();
		if (connection != null) {
			if (connection.isOpened()) {
				//TODO
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		createMenu(menu);

		// menu.add(Menu.NONE, 0, 2, "Settings");
		return true;
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		super.onOptionsMenuClosed(menu);
		menu.clear();
		createMenu(menu);
	}

	private void createMenu(Menu menu) {
		menu.add(Menu.NONE, 0, 0, "Beenden");
		menu.add(Menu.NONE, 0, 2, "Connect");
	}

	@Override
	/*
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = super.onOptionsItemSelected(item);
		if (item.getTitle() == "Beenden") {
			NXTCommBluecove connection = getConnection();
			if (connection != null && connection.isOpened()) {
				connection.stopMotor();
			}
			closeConnection();
			this.finish();
		} else if (item.getTitle() == "Connect") {
			NXTCommBluecove connection = getConnection();
			if (connection.initConnection()) {
				try {
					connection.search("", NXTCommFactory.BLUETOOTH);
				} catch (NXTCommException e) {
					e.printStackTrace();
				}
			}
		} else if (item.getTitle() == "Disconnect") {
			closeConnection();
		} else if (item.getTitle() == "Stop") {
			NXTCommBluecove connection = getConnection();
			connection.stopMotor();
		} else if (item.getTitle() == "Settings") {
			this.startActivityForResult(choiceDeviceIntent, REQUEST_SETTINGS);
		}
		return result;
	}
	*/

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
			/*
			 * TODO
			 */
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

	public void showDevices(){
//		ArrayList<String> keys=new ArrayList<String>();
//		ArrayList<String> values=new ArrayList<String>();
//		for(BluetoothDevice device : devices){
//			keys.add(device.getName());
//			values.add(device.getAddress());
//		}
//		this.owner.showDevices(keys.toArray(new String[keys.size()]), values.toArray(new String[values.size()]));			
		showDevices(devices);
	}
	
}