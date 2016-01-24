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
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class WifiMainActivity extends Activity {
	private static final String TAG = "Pedometer";
	
	public int mutex =1;
	
	
	//------------------WIFI
	TextView mainText;
	public  static ItemDAO itemDAO ;
	WifiManager mainWifi;
	BroadcastReceiver receiverWifi;
	public static boolean goback_check=false;
	 public static List<ScanResult> wifiList; //�Ҧ�WIFI SCAN ��RESULT
	private int times_of_repeat=0;
	public static String[] WifiAry;
	static List<Item> items;
	//TextView result_text;
	private boolean check=false;
	HashMap<String, String> returnValue = new HashMap<String, String>();
	//------------------WIFI
	TextView tvHeading;
	//------------------Timer
	StringBuilder sb = new StringBuilder();
	Timer timerAsync;
	TimerTask timerTaskAsync;
	private static boolean stopping = false;
	ArrayAdapter<String> adapter;
	//------------------Timer
	float[] degree = new float[3];
	//------------------Progress bar
	public  float gravity[] = new float[3];
	ProgressBar progressbar;
	//------------------Progress bar
	//------Steps
	private SharedPreferences mSettings;
    private PedometerSettings mPedometerSettings;
    private Utils mUtils;
	private SensorManager mSensorManager;
	public String turn ;
	public  float previousDegree=0;

    private TextView mStepValueView;
    private TextView mPaceValueView;
    private TextView mDistanceValueView;
    private TextView mSpeedValueView;
    private TextView mCaloriesValueView;
    TextView mDesiredPaceView;
    public  int mStepValue;
    private int mPaceValue;
    public  float mDistanceValue;
    public  float mSpeedValue;
    private float mDesiredPaceOrSpeed;
    private int mMaintain;
    private boolean mIsMetric;
    private float mMaintainInc;
    private boolean mQuitting = false;
    private boolean mIsRunning;
	 
    private Compass compass_acceler;
    private Compass compass_gravity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_main);
		
		mainText = (TextView) findViewById(R.id.mainText);
		mainText.setText("");
		tvHeading = (TextView) findViewById(R.id.tv);
		progressbar=(ProgressBar)findViewById(R.id.progressBar1);
		checkwifi();
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		
		Button searchbutton = (Button) findViewById(R.id.WifiButton);
		searchbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				progressbar.setVisibility(View.VISIBLE);
				StartSearch();
				goback_check=false;
				StartCountSteps();
				new AccelerometerDetect(WifiMainActivity.this,stopping);
				new PostDataAsyncTask(WifiMainActivity.this)
				.execute(0);//0�N��O�h
				
				
				
			}
		});
		
		
		Button gobackbutton = (Button) findViewById(R.id.gobackbutton);
		gobackbutton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				goback_check=true;
				times_of_repeat=0;
				mStepValue = 0;
		        mPaceValue = 0;
				new PostDataAsyncTask(WifiMainActivity.this)
				.execute(1,times_of_repeat);//�N��O�^��

			}});
		
		
		
	}
	
	private void  StartCountSteps(){
        mStepValue = 0;
        mPaceValue = 0;
        mUtils = Utils.getInstance();
		//-----------        
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mPedometerSettings = new PedometerSettings(mSettings);
        
        mUtils.setSpeak(mSettings.getBoolean("speak", false));
        
        // Read from preferences if the service was running on the last onPause
        mIsRunning = mPedometerSettings.isServiceRunning();
        
        // Start the service if this is considered to be an application start (last onPause was long ago)
        if (!mIsRunning && mPedometerSettings.isNewStart()) {
            startStepService();
            bindStepService();
        }
        else if (mIsRunning) {
            bindStepService();
        }
        
        mPedometerSettings.clearServiceRunning();

        mStepValueView     = (TextView) findViewById(R.id.step_value);
        mDistanceValueView = (TextView) findViewById(R.id.distance_value);
        mSpeedValueView    = (TextView) findViewById(R.id.speed_value);
       
		
	}
    private void savePaceSetting() {
        mPedometerSettings.savePaceOrSpeedSetting(mMaintain, mDesiredPaceOrSpeed);
    }

    private StepService mService;
    
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((StepService.StepBinder)service).getService();

            mService.registerCallback(mCallback);
            mService.reloadSettings();
            
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };
    

    private void startStepService() {
        if (! mIsRunning) {
            Log.i(TAG, "[SERVICE] Start");
            mIsRunning = true;
            startService(new Intent(WifiMainActivity.this,
                    StepService.class));
        }
    }
    
    private void bindStepService() {
        Log.i(TAG, "[SERVICE] Bind");
        bindService(new Intent(WifiMainActivity.this, 
                StepService.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
    }

    private void unbindStepService() {
        Log.i(TAG, "[SERVICE] Unbind");
        unbindService(mConnection);
    }
    
    private void stopStepService() {
        Log.i(TAG, "[SERVICE] Stop");
        if (mService != null) {
            Log.i(TAG, "[SERVICE] stopService");
            stopService(new Intent(WifiMainActivity.this,
                  StepService.class));
        }
        mIsRunning = false;
    }
    
    

    /* Handles item selections */
 
 
    // TODO: unite all into 1 type of message
    private StepService.ICallback mCallback = new StepService.ICallback() {
        public void stepsChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
        }
        public void paceChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, value, 0));
        }
        public void distanceChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG, (int)(value*1000), 0));
        }
        public void speedChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG, (int)(value*1000), 0));
        }
        public void caloriesChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG, (int)(value), 0));
        }
    };
    
    private static final int STEPS_MSG = 1;
    private static final int PACE_MSG = 2;
    private static final int DISTANCE_MSG = 3;
    private static final int SPEED_MSG = 4;
    private static final int CALORIES_MSG = 5;
    
    private Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case STEPS_MSG:
                    mStepValue = (int)msg.arg1;
                    mStepValueView.setText("Steps: " + mStepValue);
                    break;
               
                case DISTANCE_MSG:
                    mDistanceValue = ((int)msg.arg1)/1000f;
                    if (mDistanceValue <= 0) { 
                        mDistanceValueView.setText("0");
                    }
                    else {
                        mDistanceValueView.setText(
                                ("Distance: " + (mDistanceValue + 0.000001f)).substring(0, 15)
                        );
                    }
                    break;
                case SPEED_MSG:
                    mSpeedValue = ((int)msg.arg1)/1000f;
                    if (mSpeedValue <= 0) { 
                        mSpeedValueView.setText("0");
                    }
                    else {
                        mSpeedValueView.setText(
                                ("Speed: " + (mSpeedValue + 0.000001f)).substring(0, 15)
                        );
                    }
                    break;
             
                default:
                    super.handleMessage(msg);
            }
        }
        
    };
	
	protected void onPause() {
		//unregisterReceiver(receiverWifi);
		super.onPause();
	}

	protected void onResume() {

		super.onResume();
	}

	public class MyTimerTask extends TimerTask {
		
		@Override
		public void run() {
			
			if (stopping == false) {
				
				
				mutex =0;
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
				
				Log.i("penis", previousDegree+"   一");
				Log.i("penis", degree[0]+"   二");
				
				if(degree[0]-previousDegree>45){//向右轉
					
					turn=" RIGHT";
					
				}
				else if(degree[0]-previousDegree<-45){//向左轉
					
					turn="LEFT";
					
					
				}
				else{//直走
					turn="STRAIGHT";
					
					
				}
				previousDegree=degree[0];			
				
				mutex=1;
				
				
				
				
				
				
				
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
	public double calculateDistance(double levelInDb, double freqInMHz) {// ��Rssi�ন�Z��
		double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math
				.abs(levelInDb)) / 20.0;
		return Math.pow(10.0, exp);
	}

	private String ConvertTImeStamp(Long time) { // �n��UNIX time�N�O1970�}�l
		Date date = new Date(time); // *1000 is to convert seconds to
									// milliseconds
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
		String formattedDate = sdf.format(date);
		return formattedDate;
	}
	public void StartSearch() {
				stopping =false;
				
				Sensor accelerometer = mSensorManager
						.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
				Sensor magnetometer = mSensorManager
						.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
				mSensorManager.registerListener(compass_acceler=new Compass(WifiMainActivity.this), accelerometer,
						SensorManager.SENSOR_DELAY_NORMAL);
				mSensorManager.registerListener(compass_acceler, magnetometer,
						SensorManager.SENSOR_DELAY_NORMAL);
				
			
				
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
	 * @�o��O�]�wmenu
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
					mSensorManager.unregisterListener(compass_acceler);
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
