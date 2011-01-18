package de.uni_kassel.android.lego.all;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class DeviceSelection extends Activity {
	public static String KEY="DEVICESKEY";
	public static String VALUES="DEVICESVALUES";
	public static String DEVICEKEY="SELECTEDDEVICEKEY";
	public static String DEVICEMAC="SELECTEDDEVICEMAC";
	public static int SUCCESS=1;
	public static int CANCEL=2;
	private HashMap<String, String> devices=new HashMap<String, String>();
	private String itemKey;
	private String mac;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deviceselection);
        
        restoreInstance(this.getIntent().getExtras());
        
        Button connectBtn = (Button) findViewById(R.id.ConnectBtn);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	executeConnect();
            }
        });
	}
    public void restoreInstance(Bundle extras){
    	if(extras!=null){
	    	if(extras.containsKey(KEY)){
	    		String[] deviceKeys=(String[]) extras.get(KEY);
	    		String[] deviceValues=(String[]) extras.get(VALUES);
	    		for(int z=0;z<deviceKeys.length;z++){
	    			//String[] value=deviceString[z].split(",");
	    			devices.put(deviceKeys[z], deviceValues[z]);
	    		}
	    		ArrayAdapter<String> spinnerWeekdayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, devices.keySet().toArray(new String[devices.keySet().size()]));
	        	Spinner deviceSpinner = (Spinner) findViewById(R.id.Devices);
	        	deviceSpinner.setAdapter(spinnerWeekdayAdapter);
	    	}
    	}
    }
    public void executeConnect(){
    	Spinner deviceSpinner = (Spinner) findViewById(R.id.Devices);
    	this.itemKey = deviceSpinner.getSelectedItem().toString();
    	this.mac = devices.get(getSelectedDeviceName());
    	Intent mainIntent = new Intent().setClass(this, Startup.class);
		mainIntent.putExtra(DeviceSelection.DEVICEKEY, getSelectedDeviceName());
		mainIntent.putExtra(DeviceSelection.DEVICEMAC, getSelectedDeviceMac());
    	this.setResult(SUCCESS, mainIntent);
		this.finish();
    }
	public String getSelectedDeviceName() {
		return itemKey;
	}
	public String getSelectedDeviceMac() {
		return mac;
	}
}
