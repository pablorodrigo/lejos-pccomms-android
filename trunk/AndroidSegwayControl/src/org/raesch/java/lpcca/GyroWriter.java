package org.raesch.java.lpcca;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View.OnClickListener;

public class GyroWriter extends Service implements Runnable, SensorEventListener {

	public DataOutputStream outputStream;
	private String mTAG = "gyrowriter";
	private Context ctx;
	public static double x, y, z;

	public GyroWriter(Context ctx) {
		this.ctx = ctx;
	}	

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent arg0) {
		synchronized (this) {
			x = Math.toDegrees(arg0.values[0]);
			y = Math.toDegrees(arg0.values[1]);
			z = Math.toDegrees(arg0.values[2]);			
		}
//		long timestamp = System.nanoTime() / 1000; //= arg0.timestamp;
//		try {
//			outputStream.writeChars("" + timestamp);
//			outputStream.writeChars(";" + x);
//			outputStream.writeChars(";" + y);
//			outputStream.writeChars(";" + z + "\n");
//			outputStream.flush();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	@Override
	public void run() {
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
		Log.d(mTAG , "storage writable"+mExternalStorageWriteable);
		if(mExternalStorageWriteable){
			File path = Environment.getExternalStorageDirectory();
			File output = new File(path, "gyro_values.txt");
			OutputStream fileOutputStream;
			try {
				fileOutputStream = new FileOutputStream(output);
				outputStream = new DataOutputStream(fileOutputStream);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SensorManager sManager = (SensorManager) ctx.getSystemService(SENSOR_SERVICE);
	        sManager.registerListener(this, sManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),SensorManager.SENSOR_DELAY_FASTEST);
		}else{
			Log.d("BTGyroLogger", "no storage, not storing data");
		}
		
       
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
