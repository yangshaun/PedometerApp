package com.example.wifiinfoapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

public class Compass implements SensorEventListener {
	float[] mGravity;
	float[] mGeomagnetic;
	float Rotation[] = new float[9];
	private static WifiMainActivity WifiReceiver;
	Compass(WifiMainActivity WifiReceiver) {
		this.WifiReceiver = WifiReceiver;
	}
	
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			mGravity = event.values;
		}
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			mGeomagnetic = event.values;
		}
		if (mGravity != null && mGeomagnetic != null) {
			if(WifiReceiver.mutex!=0){
			SensorManager.getRotationMatrix(Rotation, null, mGravity,
					mGeomagnetic);
			SensorManager.getOrientation(Rotation, WifiReceiver.degree);
			
			WifiReceiver.degree[0] = (float) Math.toDegrees(WifiReceiver.degree[0]);
		
			WifiReceiver.tvHeading.setText("Heading: " + (int) WifiReceiver.degree[0] + " degrees");
			}
			
			
		}
	}

}
