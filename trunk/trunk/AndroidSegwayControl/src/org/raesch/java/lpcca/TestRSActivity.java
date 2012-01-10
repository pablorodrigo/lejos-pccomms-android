package org.raesch.java.lpcca;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lejos.pc.comm.NXTCommBluecove;
import lejos.pc.comm.NXTCommInputStream;

import org.raesch.java.lpcca.service.InterfaceLPCCARemoteService;
import org.raesch.java.lpcca.service.LPCCARemoteService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TestRSActivity extends Activity {
	private Button forwardButton;
	private Button bindServiceButton;
	private Button startServiceButton;
	private Button requestConnectionButton;
	private Button backwardButton;
	private Button leftButton;
	private Button rightButton;
	private Button stopButton;
	private GyroWriter gw;
	InterfaceLPCCARemoteService myRemoteService = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		requestConnectionButton = (Button) findViewById(R.id.button2);
		startServiceButton = (Button) findViewById(R.id.button1);
		bindServiceButton = (Button) findViewById(R.id.button3);
		forwardButton = (Button) findViewById(R.id.button4);
		backwardButton = (Button) findViewById(R.id.button5);
		leftButton = (Button) findViewById(R.id.button6);
		rightButton = (Button) findViewById(R.id.button7);
		stopButton = (Button) findViewById(R.id.button8);

		requestConnectionButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				requestConnection();
			}
		});

		startServiceButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startService(new Intent(LPCCARemoteService.class.getName()));
			}
		});

		bindServiceButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				bindService();
			}
		});

		forwardButton.setOnClickListener(new OnClickListener() {
			

			@Override
			public void onClick(View v) {
				try {
					//myRemoteService.get().forward();
					startBTGyroReaderThread();
					startGyroReaderThread();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			private void startGyroReaderThread() {
				gw = new GyroWriter(TestRSActivity.this);
				Thread btGyroThread = new Thread(gw);
				btGyroThread.start();
			}

			private void startBTGyroReaderThread() throws FileNotFoundException {
				boolean mExternalStorageAvailable = false;
				boolean mExternalStorageWriteable = false;
				String state = Environment.getExternalStorageState();

				if (Environment.MEDIA_MOUNTED.equals(state)) {
				    // We can read and write the media
				    mExternalStorageAvailable = mExternalStorageWriteable = true;
				} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
				    // We can only read the media
				    mExternalStorageAvailable = true;
				    mExternalStorageWriteable = false;
				} else {
				    // Something else is wrong. It may be one of many other states, but all we need
				    //  to know is we can neither read nor write
				    mExternalStorageAvailable = mExternalStorageWriteable = false;
				}
				if(mExternalStorageWriteable){
					File path = Environment.getExternalStorageDirectory();
					File output = new File(path, "btgyro_values.txt");
					OutputStream fileOutputStream = new FileOutputStream(output);
					final DataOutputStream outputStream = new DataOutputStream(fileOutputStream);
					NXTCommBluecove mNXTCB = NXTCommBluecove.instance;
					final InputStream is = mNXTCB.getInputStream();
					final DataInputStream dis = new DataInputStream(is);
					Thread gyroThread = new Thread(new Runnable() {
						
						@Override
						public void run() {
							while(!Thread.interrupted()){
								double d;
								try {
									d = dis.readDouble();
									try {
										long timestamp = System.nanoTime() / 1000; // System.currentTimeMillis();
										outputStream.writeChars("" + timestamp);
										double x, y, z;
										synchronized (gw) {
											x = gw.x;
											y = gw.y;
											z = gw.z;											
										}
										outputStream.writeChars(";" + x);
										outputStream.writeChars(";" + y);
										outputStream.writeChars(";" + z);
										outputStream.writeChars(";" + d + "\n");
										outputStream.flush();
									} catch (IOException e) {
										// write to file failed
										e.printStackTrace();
										return;
									}
								} catch (IOException e) {
									// read from bt failed or EOF
									e.printStackTrace();
									return;
								}
							}
						}
					});
					gyroThread.start();
				}else{
					Log.d("BTGyroLogger", "no storage, not storing data");
				}
			}
		});

		backwardButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					myRemoteService.get().backward();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		leftButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					myRemoteService.get().left();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		rightButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					myRemoteService.get().right();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					myRemoteService.get().stop();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	void requestConnection() {
		if (myRemoteService != null) {
			try {
				myRemoteService.requestConnectionToNXT();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void bindService() {
		boolean success = bindService(
				new Intent(LPCCARemoteService.class.getName()),
				serviceConnection, Context.BIND_AUTO_CREATE);
		bindServiceButton.setText(Boolean.toString(success));
	}

	private boolean boundService = false;
	private final ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			myRemoteService = InterfaceLPCCARemoteService.Stub
					.asInterface(service);
			boundService = true;
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (boundService) {
			unbindService(serviceConnection);
		}
	};
}