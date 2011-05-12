package org.raesch.java.lpcca;

import org.raesch.java.lpcca.service.InterfaceLPCCARemoteService;
import org.raesch.java.lpcca.service.LPCCARemoteService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
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
	InterfaceLPCCARemoteService myRemoteService = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		requestConnectionButton = (Button) findViewById(R.id.button2);
		startServiceButton = (Button) findViewById(R.id.button1);

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
					myRemoteService.get().forward();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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