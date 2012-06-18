package com.monkey.duda.sample.app;

import com.monkey.duda.service.IMonkeyService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ProjectBActivity extends Activity {
	
	Button serviceButton=null;
	Button messageButton=null;
	TextView logWindow=null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        logWindow = (TextView) findViewById(R.id.logwindow);
        
        serviceButton = (Button) findViewById(R.id.service);
        serviceButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				toggleService();
			}
		});
        
        messageButton = (Button) findViewById(R.id.send_msg);
        messageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mIRemoteService==null){
					logWindow.append("\nCan't send message now, Service not connected");
				}
				else{
					try {
						logWindow.append("\n"+mIRemoteService.sendAndRecieveHello("hello sent from app"));
					} catch (RemoteException e) {
						logWindow.append("Remote Exception : "+e.toString());
						e.printStackTrace();
					}
				}
			}
		});
    }
    
    public void toggleService(){
    	if(mIRemoteService==null){
    		Log.d("PAPP", "Starting Service");
    		Intent i=new Intent();
    		i.setClassName("com.monkey.duda.service", "com.monkey.duda.service.MonkeyService");
    		boolean st=bindService(i, mConnection, getApplicationContext().BIND_AUTO_CREATE);
    		if(st)
    			serviceButton.setText("Stop Service");
    		Log.d("PAPP", "Start Service result : "+st);
    	}
    	else{
    		Log.d("PAPP", "Stopping Service");
    		unbindService(mConnection);
    		mIRemoteService = null;
    		serviceButton.setText("Start Service");
    	}
    }
    
    IMonkeyService mIRemoteService;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mIRemoteService = IMonkeyService.Stub.asInterface(service);
            if(mIRemoteService !=null)
            	logWindow.append("\nService running");;
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.e("MonkeyAPP", "Service has unexpectedly disconnected");
            logWindow.append("Service has unexpectedly disconnected");
            mIRemoteService = null;
        }
    };
}