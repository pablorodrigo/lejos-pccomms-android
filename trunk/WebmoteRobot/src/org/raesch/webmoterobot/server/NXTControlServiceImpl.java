package org.raesch.webmoterobot.server;

import org.raesch.java.lpcca.service.InterfaceLPCCARemoteService;
import org.raesch.webmoterobot.client.NXTControlService;
import org.raesch.webmoterobot.shared.Action;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class NXTControlServiceImpl extends RemoteServiceServlet implements
		NXTControlService {

	String proofOfLife = null;
	private Context androidContext;
	static String LOGTAG = "LPCCA WebApp";
	InterfaceLPCCARemoteService myRemoteService = null;
	private boolean setup = false;

	public void setup() {
		Log.d(LOGTAG, "Setup called, initialising connection to service.");
		androidContext = (Context) getServletContext().getAttribute(
				"org.mortbay.ijetty.context");
		androidContext.bindService(new Intent(
				"org.raesch.java.lpcca.service.LPCCARemoteService"),
				serviceConnection, Context.BIND_AUTO_CREATE);
		setup = true;
	}

	public void runAction(Action action) throws IllegalArgumentException {
		if (!setup) {
			setup();
		}
		Log.d(LOGTAG, "Received action request: " + action.name());
		try {
			switch (action) {
			case FORWARD:
				myRemoteService.get().forward();
				break;

			case BACKWARD:
				myRemoteService.get().backward();
				break;

			case LEFT:
				myRemoteService.get().left();
				break;

			case RIGHT:
				myRemoteService.get().right();
				break;

			case STOP:
				myRemoteService.get().stop();
				break;

			case REQUEST:
				requestConnection();
				break;

			default:
				break;
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void requestConnection() {
		Log.d(LOGTAG, "Requesting connection to NXT. This will start the activity.");
		if (myRemoteService != null) {
			try {
				myRemoteService.requestConnectionToNXT();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Log.d(LOGTAG,
					"Requesting connection but myRemoteService not bound.");
		}
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(LOGTAG, "Service connected");
			myRemoteService = InterfaceLPCCARemoteService.Stub
					.asInterface(service);
			requestConnection();
		}
	};

}
