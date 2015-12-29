package com.example.wifiinfoapp;


import java.util.List;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.widget.TextView;
import android.widget.Toast;

public class MyService extends Service {
	WifiManager wifi;
	List<ScanResult> results;
	boolean stopFlag = false;
	Handler scanHandler = new Handler ();
	Runnable UpdateUIResults = new Runnable () {
		public void run () {
			updateUI ();
		}

		public void updateUI() {
		//	MainActivity.parent.removeAllViews();
			TextView tv;
			for (int i = 0; i < results.size(); i++) {
				tv = new TextView(getApplicationContext());
				tv.setText("SSID is "+results.get(i).SSID+" with strength: "+results.get(i).level);
				tv.setTextColor(Color.BLACK);
			//	MainActivity.parent.addView(tv);
				//System.out.println("SSID is "+results.get(i).SSID+" with strength: "+results.get(i).level);
			}
		}
	}; 
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	public MyService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
    public void onCreate() {
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (wifi.isWifiEnabled()) {
			Thread thread = new Thread(){
				int counter = 0;
				public void run(){
					//System.out.println("Thread Running");
					while (counter % 2 == 0 && stopFlag==false) {
						System.out.println("service Thread "+ counter);
						wifi.startScan();
						results = wifi.getScanResults();
						System.out.println(results);
						scanHandler.post (UpdateUIResults);
						try {
							//sleep for 3 secs
							Thread.sleep(3000);
							counter += 2;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			};
			thread.start(); 
		}else{
			Toast.makeText(this, "Please enable WIFI network", Toast.LENGTH_LONG).show();
		}
    }
 
    @Override
    public void onStart(Intent intent, int startId) {
    }
 
    @Override
    public void onDestroy() {
    	stopFlag = true;
    //	MainActivity.parent.removeAllViews();
        //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        
    }
}