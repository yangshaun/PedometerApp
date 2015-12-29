package com.example.wifiinfoapp;

import java.security.Timestamp;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/** Wifi receiver */
public class WifiReceiver extends BroadcastReceiver {
	
	private static  WifiMainActivity MainContext;
	public WifiReceiver(WifiMainActivity WifiMainActivity){
		 WifiReceiver.MainContext=WifiMainActivity;
	}
	
	
	

	public  void  onReceive(Context c, Intent intent) {
		//Log.i("SHOW", "AFTER");
		/**@這邊設定wifiList的value*/
		WifiMainActivity.wifiList =MainContext.mainWifi.getScanResults();
		//Log.i("AAAA",String.valueOf(MainContext.mainWifi.getScanResults().size()));
		WifiMainActivity.WifiAry = new String[WifiMainActivity.wifiList.size()];
		
		

		MainContext.sb = new StringBuilder();
		MainContext.progressbar.setVisibility(View.GONE);
		MainContext.sb.append("Number Of Wifi connections :"
				+ WifiMainActivity.wifiList.size());
		MainContext.mainText.setText(MainContext.sb);

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
					+"("+MainContext.gravity[0]+","
					+MainContext.gravity[1]+","
					+MainContext.gravity[2]+")";

		}
		//WifiReceiver.PutWifiData();
		MainContext.setview();

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
	

}