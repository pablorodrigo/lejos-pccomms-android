package de.uni_kassel.android.lego.all.test;

import de.uni_kassel.android.lego.all.AndroidBTConnectionActivity;
import de.uni_kassel.android.lego.all.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class TestActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Intent testIntent = new Intent(this, AndroidBTConnectionActivity.class);
        startActivityForResult(testIntent, AndroidBTConnectionActivity.REQUEST_INIT);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	if(requestCode == AndroidBTConnectionActivity.REQUEST_INIT && resultCode == AndroidBTConnectionActivity.CONNECTION_ESTABLISHED){
    		((TextView) findViewById(R.id.MainText)).setText("Great success!");
    	}
    	
    }
}