package org.raesch.java.lpcca.service;

import lejos.nxt.Motor;
import lejos.nxt.NXT;
import lejos.nxt.Sound;

import org.raesch.java.lpcca.AndroidBTConnectionActivity;
import org.raesch.java.lpcca.DataManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class LPCCARemoteService extends Service
{

    static String LOGTAG = "LPCCA RemoteService";

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d(LOGTAG,"Service created.");
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        Log.d(LOGTAG,"Received bind request.");
        return myRemoteService;
    }

    private final InterfaceLPCCARemoteService.Stub myRemoteService = new InterfaceLPCCARemoteService.Stub()
    {

        private DataManager dataManager;

        //@Override
        public void requestConnectionToNXT() throws RemoteException
        {
            Log.d(LOGTAG,"Trying to establish bt connection via Activity.");
            dataManager = DataManager.getInstance();
            Intent remoteIntent = new Intent(getBaseContext(),AndroidBTConnectionActivity.class);
            remoteIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplication().startActivity(remoteIntent);
            /*while(dataManager.getNxtCommBluecove() == null){
                Log.d(LOGTAG,"NXTCommBluecove not initialized.");
            }
            Sound.playTone(1000,100);
            Log.d(LOGTAG,"NXTCommBluecove initialized.");
            */
        }

        //@Override
        public Navigator get() throws RemoteException
        {
            Navigator.Stub myRemoteNavigator = new Navigator.Stub()
            {

                //@Override
                public void forward() throws RemoteException
                {

                    if (dataManager.getNxtCommBluecove() != null && dataManager.getNxtCommBluecove().isOpened())
                    {
                        Motor.A.forward();
                        Motor.B.forward();
                    }else{
                    	String error = "Something wrong happened";
                    	if(dataManager.getNxtCommBluecove() == null){
                    		error = "NXTCommBluecove == null";
                    	}else if (!dataManager.getNxtCommBluecove().isOpened()) {
                    		error = "NXTCommBluecove not open";
                    	}
                    	Log.d(LOGTAG, error);
                    }
                }

                //@Override
                public void left() throws RemoteException
                {
                    if (dataManager.getNxtCommBluecove() != null && dataManager.getNxtCommBluecove().isOpened())
                    {
                        Motor.A.backward();
                        Motor.B.forward();
                    }
                }

                //@Override
                public void right() throws RemoteException
                {
                    if (dataManager.getNxtCommBluecove() != null && dataManager.getNxtCommBluecove().isOpened())
                    {
                        Motor.A.forward();
                        Motor.B.backward();
                    }
                }

                //@Override
                public void stop() throws RemoteException
                {
                    if (dataManager.getNxtCommBluecove() != null && dataManager.getNxtCommBluecove().isOpened())
                    {
                        Motor.A.stop();
                        Motor.B.stop();
                    }
                }

                //@Override
                public void backward() throws RemoteException
                {
                    if (dataManager.getNxtCommBluecove() != null && dataManager.getNxtCommBluecove().isOpened())
                    {
                        Motor.A.backward();
                        Motor.B.backward();
                    }
                }

                //@Override
                public boolean connected() throws RemoteException
                {
                    if (dataManager.getNxtCommBluecove() != null && dataManager.getNxtCommBluecove().isOpened())
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
            };
            return myRemoteNavigator;
        }
    };
}
