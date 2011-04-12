package org.raesch.java.lpcca;

import org.raesch.java.lpcca.service.InterfaceLPCCARemoteService;
import org.raesch.java.lpcca.service.LPCCARemoteService;
import org.raesch.java.lpcca.service.Navigator;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class TestRSActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        startService(new Intent(this,LPCCARemoteService.class));
        
        bindService();
    }
    
    InterfaceLPCCARemoteService myRemoteService = null;
    Navigator myNavigator = null;
    
    private void bindService() {
    	bindService(new Intent(LPCCARemoteService.class.getName()), serviceConnection, Context.BIND_AUTO_CREATE);
    }
    
    private ServiceConnection serviceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			myRemoteService = InterfaceLPCCARemoteService.Stub.asInterface(service);
			try {
				myRemoteService.requestConnectionToNXT();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
}