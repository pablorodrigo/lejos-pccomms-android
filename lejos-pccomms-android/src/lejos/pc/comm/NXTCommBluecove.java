package lejos.pc.comm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Vector;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Implementation of NXTComm using the Bluecove libraries 
 * on Microsoft Windows. 
 * 
 * Should not be used directly - use NXTCommFactory to create
 * an appropriate NXTComm object for your system and the protocol
 * you are using.
 *
 */
public class NXTCommBluecove implements NXTComm {
    private static Vector<NXTInfo> nxtInfos;
	private OutputStream os;
	private InputStream is;
	//private NXTInfo nxtInfo;
	private BluetoothDevice connecteddevice=null;
	private BluetoothSocket mmSocket;

    private static final int SCANTIME=10;
	private BroadcastReceiver mReceiverFound=null;
	private BroadcastReceiver mReceiverScanMode=null;
	private Vector<BluetoothDevice> devices;
	private boolean isopened=false;
	
	public static NXTCommBluecove instance;
	private Timer resetScan=null;
	

	public NXTInfo[] search(String name, int protocol) throws NXTCommException {
		devices = new Vector<BluetoothDevice>();
		nxtInfos = new Vector<NXTInfo>();

		if ((protocol & NXTCommFactory.BLUETOOTH) == 0){
			return new NXTInfo[0];
		}
		if(mBluetoothAdapter==null){
			if(!initConnection()){
				return null;
			}
		}
		if(mBluetoothAdapter==null){
			return null;
		}
		devices.clear();
		// If there are paired devices
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
		    for (BluetoothDevice device : pairedDevices) {
		        // Add the name and address to an array adapter to show in a ListView
		    	devices.add(device);
		    }
		}
		if (!mBluetoothAdapter.isDiscovering()) {
			scanBlueTooth();
		}

		return nxtInfos.toArray(new NXTInfo[nxtInfos.size()]);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  switch (requestCode) {
	  case REQUEST_CONNECT_DEVICE:
	      // When DeviceListActivity returns with a device to connect
	      if (resultCode == Activity.RESULT_OK) {
	          // Get the device MAC address
//		          String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
	          // Get the BLuetoothDevice object
//		          BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
	          // Attempt to connect to the device
//		          mChatService.connect(device);
	      }
	      break;
	  case REQUEST_ENABLE_BT:
	      // When the request to enable Bluetooth returns
	      if (resultCode == Activity.RESULT_OK) {
	          // Bluetooth is now enabled, so set up a chat session
	    	  try {
				search("", NXTCommFactory.BLUETOOTH);
			} catch (NXTCommException e) {
			}
	      }
	      break;
	  case REQUEST_ENABLE_SCAN:
		  if (resultCode == SCANTIME) {
			  mBluetoothAdapter.startDiscovery();			  
		  }
		  break;
	  }
	}

	public BluetoothSocket getMmSocket() {
		return mmSocket;
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
			this.owner.registerReceiver(mReceiverFound, new IntentFilter(BluetoothDevice.ACTION_FOUND));

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
			this.owner.registerReceiver(mReceiverScanMode, new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));
		}
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, SCANTIME);
		this.owner.startActivityForResult(discoverableIntent, REQUEST_ENABLE_SCAN);
		resetScan = new Timer();
		resetScan.schedule(new TimerTask() {
			public void run() {
				if(NXTCommBluecove.this.devices.size()<1){
					NXTCommBluecove.this.owner.showError("Fehler beim suchen");
				}else{
					showDevices();
				}
			}
		}, SCANTIME*2*1000);
		//this.owner.startActivity(discoverableIntent);
	}
	public void removeYou(){
		if(mReceiverFound!=null){
			this.owner.unregisterReceiver(mReceiverFound);
		}
		if(mReceiverScanMode!=null){
			this.owner.unregisterReceiver(mReceiverScanMode);
		}
	}

	@Override
	public boolean open(NXTInfo nxt, int mode) throws NXTCommException {
		if (mode == RAW) throw new NXTCommException("RAW mode not implemented");
		// Construct URL if not present

		if (nxt.btResourceString == null || nxt.btResourceString.length() < 5
				|| !(nxt.btResourceString.substring(0, 5).equals("btspp"))) {
			nxt.btResourceString = "btspp://"
					+ stripColons(nxt.deviceAddress)
					+ ":1;authenticate=false;encrypt=false";
		}

		try {
			if(mBluetoothAdapter==null){
				if(!initConnection()){
					return false;
				}
			}
			if(mBluetoothAdapter==null){
				return false;
			}
			connecteddevice = mBluetoothAdapter.getRemoteDevice(nxt.deviceAddress);
			if(connecteddevice!=null){
				BluetoothSocket tmp =null;
			    // Get a BluetoothSocket to connect with the given BluetoothDevice
	            // MY_UUID is the app's UUID string, also used by the server code
	        	//00001101-0000-1000-8000-00805F9B34FB.
	        	UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	        	tmp = connecteddevice.createRfcommSocketToServiceRecord(uuid);

		    	this.mmSocket=tmp;
		    	this.mmSocket.connect();
		    	instance=this;
			}
			os = this.mmSocket.getOutputStream();
			is = this.mmSocket.getInputStream();
			nxt.connectionState = (mode == LCP ? NXTConnectionState.LCP_CONNECTED : NXTConnectionState.PACKET_STREAM_CONNECTED);
			isopened=true;
			return true;
		} catch (IOException e) {
			nxt.connectionState = NXTConnectionState.DISCONNECTED;
			throw new NXTCommException("Open of " + nxt.name + " failed: " + e.getMessage());
		}
	}

    public boolean open(NXTInfo nxt) throws NXTCommException
    {
        return open(nxt, PACKET);
    }

	public void close() throws IOException {
		if (os != null){
			os.close();
			os=null;
		}
		if (is != null){
			is.close();
			is=null;
		}
		if (mmSocket != null){
			mmSocket.close();
			mmSocket=null;
		}
		this.isopened=false;
	}

	/**
	 * Sends a request to the NXT brick.
	 * 
	 * @param message
	 *            Data to send.
	 */
	public synchronized byte[] sendRequest(byte[] message, int replyLen)
			throws IOException {

		// length of packet (Least and Most significant byte)
		// * NOTE: Bluetooth only. 
		int LSB = message.length;
		int MSB = message.length >>> 8;

		if (os == null)
			return new byte[0];

		// Send length of packet:
		os.write((byte) LSB);
		os.write((byte) MSB);

		os.write(message);
		os.flush();

		if (replyLen == 0)
			return new byte[0];

		byte[] reply = null;
		int length = -1;

		if (is == null)
			return new byte[0];

		do {
			length = is.read(); // First byte specifies length of packet.
		} while (length < 0);

		int lengthMSB = is.read(); // Most Significant Byte value
		length = (0xFF & length) | ((0xFF & lengthMSB) << 8);
		reply = new byte[length];
		int len = is.read(reply);
		if (len != replyLen) throw new IOException("Unexpected reply length");

		return (reply == null) ? new byte[0] : reply;
	}

	public byte[] read() throws IOException {

        int lsb = is.read();
		if (lsb < 0) return null;
		int msb = is.read();
        if (msb < 0) return null;
        int len = lsb | (msb << 8);
		byte[] bb = new byte[len];
		for (int i=0;i<len;i++) bb[i] = (byte) is.read();

		return bb;
	}
	
    public int available() throws IOException {
        return 0;
    }

	public void write(byte[] data) throws IOException {
        os.write((byte)(data.length & 0xff));
        os.write((byte)((data.length >> 8) & 0xff));
		os.write(data);
		os.flush();
	}

	public OutputStream getOutputStream() {
		return new NXTCommOutputStream(this);
	}

	public InputStream getInputStream() {
		return new NXTCommInputStream(this);
	}
	
	public InputStream getRAWInputStream() {
		return is;
	}
	
	public void setInputStream(InputStream is) {
		this.is = is;
	}

	public String stripColons(String s) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if (c != ':') {
				sb.append(c);
			}
		}

		return sb.toString();
	}
	
	public boolean isOpened() {
		if(this.mmSocket==null){
			return false;
		}
		return isopened; 
	}
}
