package org.raesch.java.lpcca.service;

import lejos.nxt.Motor;
import lejos.pc.comm.NXTCommBluecove;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;
import org.raesch.java.lpcca.AndroidBTConnectionActivity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class LPCCARemoteService extends Service {

	static String LOGTAG = "LPCCA RemoteService";
	private BluetoothAdapter mBluetoothAdapter;
	public static NXTCommBluecove myNXTCommBluecove;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(LOGTAG, "Service created.");
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(LOGTAG, "No bluetooth adapter found.");
		} else {
			Log.d(LOGTAG, "Bluetooth adapter found.");
			myNXTCommBluecove = new NXTCommBluecove(mBluetoothAdapter);
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		Log.d(LOGTAG, "Received bind request: " + arg0.toString());
		return myRemoteService;
	}

	private final InterfaceLPCCARemoteService.Stub myRemoteService = new InterfaceLPCCARemoteService.Stub() {

		private String lastKey;
		private String lastMac;

		/* (non-Javadoc)
		 * This will establish the bluetooth connection to the specified device given by key & mac.
		 * @see org.raesch.java.lpcca.service.InterfaceLPCCARemoteService#establishBTConnection(java.lang.String, java.lang.String)
		 */
		public void establishBTConnection(String deviceKey, String deviceMac) {
			if (deviceKey != null && deviceMac != null) {
				lastKey = deviceKey;
				lastMac = deviceMac;
				mBluetoothAdapter.cancelDiscovery();
				NXTInfo nxtInfo = new NXTInfo(NXTCommFactory.BLUETOOTH, deviceKey,
						deviceMac);
				try {
					myNXTCommBluecove.open(nxtInfo);
				} catch (NXTCommException e) {
				}
			}
		}
		
		// @Override
		/* (non-Javadoc)
		 * This will open the activity so the user can choose a device he wants to connect to.
		 * @see org.raesch.java.lpcca.service.InterfaceLPCCARemoteService#requestConnectionToNXT()
		 */
		public void requestConnectionToNXT() throws RemoteException {
			Log.d(LOGTAG, "Trying to establish bt connection via Activity.");
			Intent remoteIntent = new Intent(getBaseContext(),
					AndroidBTConnectionActivity.class);
			remoteIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getApplication().startActivity(remoteIntent);
		}

		// @Override
		public Navigator get() throws RemoteException {
			//only allow use of the navigator if the connection is fully established.
			if(myNXTCommBluecove != null
							&& myNXTCommBluecove.isOpened()){
				Log.d(LOGTAG, "Returning navigator, connection fully established.");
				return myRemoteNavigator;
			}else{
				Log.d(LOGTAG, "Not returning navigator, connection not fully established.");
				String error = "Something wrong happened";
				if (myNXTCommBluecove == null) {
					error = "NXTCommBluecove == null";
				} else if (!myNXTCommBluecove.isOpened()) {
					error = "NXTCommBluecove not open";
				}
				Log.d(LOGTAG, error);
				return null;
			}
		}
		
		Navigator.Stub myRemoteNavigator = new Navigator.Stub() {
			int count = 250;
			// @Override
			public void forward() throws RemoteException {
				if (myNXTCommBluecove != null
						&& myNXTCommBluecove.isOpened()) {
					//Motor.A.forward();
					//Motor.B.forward();
					Motor.A.rotate(count, true);
					Motor.B.rotate(count, true);
				}
			}
			// @Override
			public void left() throws RemoteException {
				if (myNXTCommBluecove != null
						&& myNXTCommBluecove.isOpened()) {
					//Motor.A.backward();
					//Motor.B.forward();
					Motor.A.rotate(-count, true);
					Motor.B.rotate(count, true);
				}
			}
			// @Override
			public void right() throws RemoteException {
				if (myNXTCommBluecove != null
						&& myNXTCommBluecove.isOpened()) {
					//Motor.A.forward();
					//Motor.B.backward();
					Motor.A.rotate(count, true);
					Motor.B.rotate(-count, true);
				}
			}
			// @Override
			public void stop() throws RemoteException {
				if (myNXTCommBluecove != null
						&& myNXTCommBluecove.isOpened()) {
					Motor.A.stop();
					Motor.B.stop();
				}
			}
			// @Override
			public void backward() throws RemoteException {
				if (myNXTCommBluecove != null
						&& myNXTCommBluecove.isOpened()) {
					//Motor.A.backward();
					//Motor.B.backward();
					Motor.A.rotate(-count, true);
					Motor.B.rotate(-count, true);
				}
			}
			// @Override
			public boolean connected() throws RemoteException {
				if (myNXTCommBluecove != null
						&& myNXTCommBluecove.isOpened()) {
					return true;
				} else {
					return false;
				}
			}
		};
	};
}
