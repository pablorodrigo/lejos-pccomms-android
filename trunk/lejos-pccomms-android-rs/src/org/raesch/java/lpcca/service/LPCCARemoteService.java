package org.raesch.java.lpcca.service;

import lejos.nxt.Motor;

import org.raesch.java.lpcca.AndroidBTConnectionActivity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class LPCCARemoteService extends Service {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return myRemoteService;
	}
	
	private final InterfaceLPCCARemoteService.Stub myRemoteService = new InterfaceLPCCARemoteService.Stub() {
		
		@Override
		public void requestConnectionToNXT() throws RemoteException {
			Intent remoteIntent = new Intent(getBaseContext(), AndroidBTConnectionActivity.class);
	        getApplication().startActivity(remoteIntent);
		}
		
		@Override
		public Navigator get() throws RemoteException {
			Navigator.Stub myRemoteNavigator = new Navigator.Stub() {
				
				@Override
				public void forward() throws RemoteException {
					Motor.A.forward();
		    		Motor.B.forward();
				}
			};
			return myRemoteNavigator;
		}
	};
}
