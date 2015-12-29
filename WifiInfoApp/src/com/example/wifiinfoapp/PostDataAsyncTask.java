package com.example.wifiinfoapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.format.Time;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

/**
 * @用來POST資料上去PHP資料庫
 *
 */
public class PostDataAsyncTask extends AsyncTask<Integer, String, String> {
	/**
	 * Params -- 要執行 doInBackground() 時傳入的參數，數量可以不止一個 Progress --
	 * doInBackground() 執行過程中回傳給 UI thread 的資料，數量可以不止一個 Rsesult -- 傳回執行結果，
	 * 
	 * */
	private static final String TAG = "PostDataAsyncTask";
	private static final String postReceiverUrl = "http://140.120.13.242/Wifi/wifi.php";
	private static final String getReceiverUrl = "http://140.120.13.242/Wifi/wifi2.php";
	private static final String deleteReciverUrl = "http://140.120.13.242/Wifi/wifi3.php";
	private static WifiMainActivity WifiReceiver;
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	private static String Time;
	
	int actionChoice;
	String reslutString = null;
	PostDataAsyncTask(WifiMainActivity WifiReceiver) {
		this.WifiReceiver = WifiReceiver;
	}

	@Override
	protected void onPreExecute() {
		// super.onPreExecute();

	}

	@Override
	protected String doInBackground(Integer... arg0) {
		// TODO Auto-generated method stub
		try {

			actionChoice = arg0[0].intValue();

			if (actionChoice == 0) {
				postText(arg0[0].intValue());
			}
			else if(actionChoice==2){
				cleardata();
			}
			else {
				
					getText(arg0[0].intValue(),arg0[1].intValue());
			}

		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	protected void onPostExecute(String lenghtOfFile) {
		/**
		 * 這邊是把剛剛asynctask的資料全部update to the views
		 * 
		 * */

		TextView result_text = (TextView) ((Activity) PostDataAsyncTask.WifiReceiver)
				.findViewById(R.id.resulttext); // 把集合所剩下的數目算出來
		result_text.setMovementMethod(ScrollingMovementMethod.getInstance());
		String tmp =WifiReceiver.returnValue.size()+"\n"+WifiReceiver.returnValue.keySet().toString() + " "+ WifiReceiver.returnValue.values().toString() + "\n";
		result_text.setText(tmp);
	}
	private void  cleardata() throws ClientProtocolException, IOException, URISyntaxException{
		/**
		 * 砍資料庫
		 * 
		 * */
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet();// get 資料
			request.setURI(new URI(deleteReciverUrl));
			httpClient.execute(request);
		
	}
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void getText(int go_or_back,int times_of_repeat) throws ParseException, IOException,URISyntaxException {
		
		try {
			/*
			 * 這邊是在跟資料庫連線 然後把第一次去的資料抓回來
			 * **/
			if(times_of_repeat<1){
			Log.i("這個應該只有一次","這個應該只有一次");
			String line = null;
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet();// get 資料
			request.setURI(new URI(getReceiverUrl));
			HttpResponse response = httpClient.execute(request);
			BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuilder sb = new StringBuilder();
			while ((line = in.readLine()) != null) {
				sb.append(line + "\n");//因為php先json encode了所以先把資料一行一行input近來
			reslutString = sb.toString();
			}
				JSONArray jArray = new JSONArray(reslutString);
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject json_data = jArray.getJSONObject(i);
					WifiReceiver.returnValue.put(json_data.getString("BSSID"),json_data.getString("Level"));
				}		
			}
			for (int i = 0; i < PostDataAsyncTask.WifiReceiver.wifiList.size(); i++) {
				if (WifiReceiver.returnValue.containsKey(PostDataAsyncTask.WifiReceiver.wifiList.get(i).BSSID))
					WifiReceiver.returnValue.remove(PostDataAsyncTask.WifiReceiver.wifiList.get(i).BSSID);
				
			}

		} catch (JSONException e) {
			Log.i("XXXXXX", "JSON錯了");
			e.printStackTrace();
		}catch (ClientProtocolException e) {
			e.printStackTrace();
		}

	}

	private void postText(int go_or_back) throws ParseException, IOException {

		try {

			HttpClient httpClient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost(postReceiverUrl);

			JSONArray SSID = new JSONArray();
			JSONArray BSSID = new JSONArray();
			JSONArray Level = new JSONArray();
			JSONArray TIME = new JSONArray();
			String ACCELER = "(" + PostDataAsyncTask.WifiReceiver.gravity[0]
					+ "," + PostDataAsyncTask.WifiReceiver.gravity[1] + ","
					+ PostDataAsyncTask.WifiReceiver.gravity[2] + ")";

			for (int i = 0; i < WifiMainActivity.WifiAry.length; i++) {

				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy/MM/dd HH:mm:ss");
				PostDataAsyncTask.Time = sdf.format(new Date());

				SSID.put(PostDataAsyncTask.WifiReceiver.wifiList.get(i).SSID);
				BSSID.put(PostDataAsyncTask.WifiReceiver.wifiList.get(i).BSSID);
				Level.put(String
						.valueOf(PostDataAsyncTask.WifiReceiver.wifiList.get(i).level));
				TIME.put(String.valueOf(PostDataAsyncTask.Time));
			}

			String SSIDstr = SSID.toString();
			String BSSIDstr = BSSID.toString();
			String Levelstr = Level.toString();
			String Timestr = TIME.toString();
			nameValuePairs.add(new BasicNameValuePair("ssid", SSIDstr));
			nameValuePairs.add(new BasicNameValuePair("level", Levelstr));
			nameValuePairs.add(new BasicNameValuePair("bssid", BSSIDstr));
			nameValuePairs.add(new BasicNameValuePair("time", Timestr));
			nameValuePairs.add(new BasicNameValuePair("acceler", ACCELER));
			nameValuePairs.add(new BasicNameValuePair("check", String
					.valueOf(go_or_back)));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
					HTTP.UTF_8));

			HttpResponse response = httpClient.execute(httpPost);// 送出資料給php

			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {
				String responseStr = EntityUtils.toString(resEntity).trim();
				Log.i(TAG, "Response: " + responseStr);

			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		}
	}
}
