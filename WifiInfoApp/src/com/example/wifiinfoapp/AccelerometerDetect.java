package com.example.wifiinfoapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

/**
 * @
 *
 */
public class AccelerometerDetect implements SensorEventListener  {
		
	private SensorManager aSensorManager;
	private Sensor aSensor;
	private static  WifiMainActivity AccelerometerDetectMain;
	private static boolean stop=false;
	AccelerometerDetect(WifiMainActivity WifiMainActivity,boolean stop){
		AccelerometerDetect.AccelerometerDetectMain=WifiMainActivity;
		AccelerometerDetect.stop=stop;
		if(aSensor!=null&&stop){
			aSensorManager.unregisterListener(this);
			Toast.makeText(AccelerometerDetect.AccelerometerDetectMain, "Unregister accelerometerListener", Toast.LENGTH_LONG).show();
			
		}else{
			setAccelerometer();
			
		}
		
		
		
		
		}
	
	
	public void setAccelerometer(){
		aSensorManager=(SensorManager) AccelerometerDetectMain.getSystemService(Context.SENSOR_SERVICE);
		aSensor = aSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		aSensorManager.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_NORMAL);
		
		
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if(!stop){
			AccelerometerDetectMain.gravity[0] = event.values[0];
			AccelerometerDetectMain.gravity[1] = event.values[1];
			AccelerometerDetectMain.gravity[2] = event.values[2];
		}
		
		
		
	}

}
