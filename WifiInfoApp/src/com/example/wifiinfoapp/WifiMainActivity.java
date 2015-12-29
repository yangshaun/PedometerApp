package com.example.wifiinfoapp;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class WifiMainActivity extends Activity {
	//------------------WIFI
	TextView mainText;
	public  static ItemDAO itemDAO ;
	WifiManager mainWifi;
	BroadcastReceiver receiverWifi;
	public static boolean goback_check=false;
	 public static List<ScanResult> wifiList; //所有WIFI SCAN 的RESULT
	private int times_of_repeat=0;
	public static String[] WifiAry;
	static List<Item> items;
	//TextView result_text;
	private boolean check=false;
	HashMap<String, String> returnValue = new HashMap<String, String>();
	//------------------WIFI
	
	//------------------Timer
	StringBuilder sb = new StringBuilder();
	Timer timerAsync;
	TimerTask timerTaskAsync;
	private static boolean stopping = false;
	ArrayAdapter<String> adapter;
	//------------------Timer
	
	//------------------Progress bar
	public  float gravity[] = new float[3];
	ProgressBar progressbar;
	//------------------Progress bar
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_main);
		
		mainText = (TextView) findViewById(R.id.mainText);
		mainText.setText("");
		
		progressbar=(ProgressBar)findViewById(R.id.progressBar1);
		checkwifi();
		
		
		Button searchbutton = (Button) findViewById(R.id.WifiButton);
		searchbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				progressbar.setVisibility(View.VISIBLE);
				StartSearch();
				goback_check=false;
				new AccelerometerDetect(WifiMainActivity.this,stopping);
				new PostDataAsyncTask(WifiMainActivity.this)
				.execute(0);//0代表是去
				
				
			}
		});
		
		
		Button gobackbutton = (Button) findViewById(R.id.gobackbutton);
		gobackbutton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				goback_check=true;
				times_of_repeat=0;
				new PostDataAsyncTask(WifiMainActivity.this)
				.execute(1,times_of_repeat);//代表是回來

			}});
		
		
		
	}
	
	protected void onPause() {
		//unregisterReceiver(receiverWifi);
		super.onPause();
	}

	protected void onResume() {
//		registerReceiver(receiverWifi=new BroadcastReceiver(){
//
//			@Override
//			public void onReceive(Context context, Intent intent) {
//				wifiList=mainWifi.getScanResults();
//				WifiAry = new String[WifiMainActivity.wifiList.size()];
//				dataFetching();
//				PutWifiData();
//			}}, new
//		IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		super.onResume();
	}

	public class MyTimerTask extends TimerTask {
		
		@Override
		public void run() {
			
			if (stopping == false) {						
				registerReceiver( receiverWifi =new BroadcastReceiver(){

					@Override
					public void onReceive(Context context, Intent intent) {
						wifiList=mainWifi.getScanResults();
						WifiAry = new String[WifiMainActivity.wifiList.size()];
						dataFetching();
						check=true;
					}}, new IntentFilter(
						WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
				
				mainWifi.startScan();
				
				
				new AccelerometerDetect(WifiMainActivity.this,stopping);
				if(goback_check==false)
					new PostDataAsyncTask(WifiMainActivity.this).execute(0);
				else{
					new PostDataAsyncTask(WifiMainActivity.this).execute(1,times_of_repeat);
					times_of_repeat++;
				}
					
			
			} else {
 
				timerAsync.cancel();
				timerAsync.purge();
				return;
			}
		}		
	}
	
	
	private void dataFetching(){
		sb = new StringBuilder();
		progressbar.setVisibility(View.GONE);
		sb.append("Number Of Wifi connections :"
				+ WifiMainActivity.wifiList.size());
		mainText.setText(sb);

		for (int j = 0; j < WifiMainActivity.wifiList.size(); j++) {

			WifiMainActivity.WifiAry[j] = (j + 1)
					+ ". "
					+ "Name:"
					+ WifiMainActivity.wifiList.get(j).SSID
					+ "\n"
					+ "Distance:"
					+ String.format(
							"%.3f",
							calculateDistance(
									(double) WifiMainActivity.wifiList.get(j).level,
									WifiMainActivity.wifiList.get(j).frequency))
					+ "  (m)"
					+ "\n"
					+ "BSSID: "
					+ WifiMainActivity.wifiList.get(j).BSSID
					+ "\n"
					+ "Timestamp: "
					+ ConvertTImeStamp(WifiMainActivity.wifiList.get(j).timestamp / 1000L)
					+ "\n" 
					+ "Level: " 
					+ WifiMainActivity.wifiList.get(j).level
					+"\n"
					+"Accelerometer: "
					+"\n"
					+"("+gravity[0]+","
					+gravity[1]+","
					+gravity[2]+")";

		}
		setview();
	}
	public double calculateDistance(double levelInDb, double freqInMHz) {// 把Rssi轉成距離
		double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math
				.abs(levelInDb)) / 20.0;
		return Math.pow(10.0, exp);
	}

	private String ConvertTImeStamp(Long time) { // 好像UNIX time就是1970開始
		Date date = new Date(time); // *1000 is to convert seconds to
									// milliseconds
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
		String formattedDate = sdf.format(date);
		return formattedDate;
	}
	public void StartSearch() {
				stopping =false;
		registerReceiver(receiverWifi=new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				wifiList=mainWifi.getScanResults();
				WifiAry = new String[WifiMainActivity.wifiList.size()];
				dataFetching();
				PutWifiData();
				check=true;
			}}, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		mainWifi.startScan();
		if(check==true)
			PutWifiData();
		timerAsync = new Timer();
		timerTaskAsync = new MyTimerTask();
		timerAsync.schedule(timerTaskAsync, 0, 4000);
		
	}

	public void PutWifiData() {
			
		//Log.i("SHOW!!!!!!!!!!!!!", String.valueOf(WifiMainActivity.wifiList.size()));
			itemDAO= new ItemDAO(WifiMainActivity.this);
			
			Item tmp_item;
			
			tmp_item=new Item();
				
			for(ScanResult data: wifiList){
				tmp_item.setSSID(data.SSID);
				tmp_item.setLevel(String.valueOf(data.level));
				tmp_item.setBSSID(data.BSSID);
				itemDAO.insert(tmp_item);
				//Log.i("SHOW!!!!!!!!!!!!!", String.valueOf(data.SSID));
			}
		
	}

	public void setview() {
		
		//---
		ListView listView = (ListView) findViewById(R.id.listView1);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, WifiAry);
		listView.setAdapter(adapter);

	}

	public void checkwifi() {
		mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		// Check for wifi is disabled
		if (mainWifi.isWifiEnabled() == false) {
			// If wifi disabled then enable it
			Toast.makeText(getApplicationContext(),
					"wifi is disabled..making it enabled", Toast.LENGTH_LONG)
					.show();
			mainWifi.setWifiEnabled(true);
		}

	}
	
	
	
	/**
	 * @這邊是設定menu
	 * */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add(0, 0, 0, "Refresh");
		menu.add(0, 1, 1, "Stop Searching"); 
		menu.add(0,2,2,"clear database!!!");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
			case 0:
				StartSearch();
				break;
			case 1:
				stopping = true;
				progressbar.setVisibility(View.GONE);
				if (wifiList.contains(receiverWifi)) {
					unregisterReceiver(receiverWifi);
				} else {
					Toast.makeText(this, "Stop Searching !!",
							Toast.LENGTH_SHORT).show();
				}

				break;
			case 2:
				new PostDataAsyncTask(WifiMainActivity.this).execute(2);
				break;
			default:
		}

		return super.onMenuItemSelected(featureId, item);
	}
	
	

}
