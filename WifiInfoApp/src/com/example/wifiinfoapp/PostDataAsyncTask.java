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
 * @嚙諄剁蕭POST嚙踝蕭W嚙篁PHP嚙踝蕭w
 *
 */
public class PostDataAsyncTask extends AsyncTask<Integer, String, String> {
	/**
	 * Params -- 嚙緯嚙踝蕭嚙踝蕭 doInBackground() 嚙褕傳入嚙踝蕭嚙諸數，嚙複量嚙箠嚙瘡嚙踝蕭嚙踝蕭@嚙踝蕭 Progress
	 * -- doInBackground() 嚙踝蕭嚙踝蕭L嚙緹嚙踝蕭嚙稷嚙褒蛛蕭 UI thread
	 * 嚙踝蕭嚙踝蕭A嚙複量嚙箠嚙瘡嚙踝蕭嚙踝蕭@嚙踝蕭 Rsesult -- 嚙褒回嚙踝蕭嚙賣結嚙瘦嚙璀
	 * 
	 * */
	private static final String TAG = "PostDataAsyncTask";
	private static final String postReceiverUrl = "http://140.120.13.251:8080/insertdata.php";
	private static final String getReceiverUrl = "http://140.120.13.242/Wifi/wifi2.php";
	private static final String deleteReciverUrl = "http://140.120.13.242/Wifi/wifi3.php";
	private static final String getDISTINCTvalueURL = "http://140.120.13.242/Wifi/wifi4.php";
	private static final String postDISTINCTvalueURL = "http://140.120.13.242/Wifi/wifi5.php";
	private static WifiMainActivity WifiReceiver;
	private int count_for_BSSID;
	private ArrayList<int[]> List_of_Vectors = new ArrayList<int[]>();

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
				postText(arg0[0].intValue(), arg0[1].intValue());
			} else if (actionChoice == 2) {
				cleardata();
			} else {

				getText(arg0[0].intValue(), arg0[1].intValue()); // go back
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
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	protected void onPostExecute(String lenghtOfFile) {
		/**
		 * 嚙緻嚙踝蕭O嚙踝蕭嚙踝蕭asynctask嚙踝蕭嚙踝蕭嚙踝蕭嚙線pdate to the views
		 * 
		 * */

		TextView result_text = (TextView) ((Activity) PostDataAsyncTask.WifiReceiver)
				.findViewById(R.id.resulttext); // 嚙賤集嚙碼嚙課剩下嚙踝蕭嚙複目綽蕭X嚙踝蕭
		result_text.setMovementMethod(ScrollingMovementMethod.getInstance());
		String tmp = WifiReceiver.returnValue.size() + "\n"
				+ WifiReceiver.returnValue.keySet().toString() + " "
				+ WifiReceiver.returnValue.values().toString() + "\n";
		result_text.setText(tmp);
	}

	private void cleardata() throws ClientProtocolException, IOException,
			URISyntaxException {
		/**
		 * 嚙踝蕭嚙複庫
		 * 
		 * */
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet();// get 嚙踝蕭嚙�
		request.setURI(new URI(deleteReciverUrl));
		httpClient.execute(request);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void getText(int go_or_back, int times_of_repeat) throws ParseException, IOException, URISyntaxException, JSONException {

		count_for_BSSID = 0;
		String line = null;
		HttpClient httpClient1 = new DefaultHttpClient();
		HttpGet request1 = new HttpGet();// get 嚙踝蕭嚙�
		request1.setURI(new URI(getDISTINCTvalueURL));
		HttpResponse response1 = httpClient1.execute(request1);
		BufferedReader in1 = new BufferedReader(new InputStreamReader(response1.getEntity().getContent()));
		StringBuilder sb1 = new StringBuilder();
		while ((line = in1.readLine()) != null) {
			sb1.append(line + "\n");
			reslutString = sb1.toString();
		}
		JSONArray jArray1 = new JSONArray(reslutString);
		count_for_BSSID = jArray1.length();
		int[] Vectors = new int[count_for_BSSID];
		String[] bssid = new String[count_for_BSSID];
		String[] time = new String[1000];///時間有可能會報
		for (int i = 0; i < count_for_BSSID; i++) {
			Vectors[i] = 0;
			bssid[i] = (String) jArray1.getJSONObject(i).get("BSSID"); 
		}

		try {

			if (times_of_repeat < 1) {
				// /---------------------------the lower code is for getting the

				line = null;
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet request = new HttpGet();
				request.setURI(new URI(getReceiverUrl));
				HttpResponse response = httpClient.execute(request);
				BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				StringBuilder sb = new StringBuilder();
				while ((line = in.readLine()) != null) {
					sb.append(line + "\n");
					reslutString = sb.toString();
				}
				// 上面是把php query出來的資料轉 string
				JSONArray jArray = new JSONArray(reslutString);

				int j = 0;
				int t = 1;

				for (int i = 0; i < jArray.length(); i++) {
					JSONObject json_data = jArray.getJSONObject(i);
					if (i == 0) {
						Vectors[0] = Integer.parseInt((String) jArray.getJSONObject(0).get("Level")); 
						time[0] = (String) jArray.getJSONObject(0).get("TIME");
						continue;
					}
					if (jArray.getJSONObject(i - 1).get("TIME").equals(json_data.get("TIME"))) {

						for (int k = 0; k < bssid.length; k++) {
							if (json_data.get("BSSID").equals(bssid[k])) {
								j = k;
								break;
							}
						}
						Vectors[j] = Integer.parseInt((String) json_data.get("Level"));
						WifiReceiver.returnValue.put(json_data.getString("BSSID"), json_data.getString("Level"));
						continue;
					}

					// //這邊要等到時間不一樣的時候才會走到此code

					time[t] = (String) jArray.getJSONObject(i).get("TIME");
					List_of_Vectors.add(Vectors.clone()); 
					int l = 0;
					for (; l < count_for_BSSID; l++) {
						Vectors[l] = 0;
					}
					t++;
					WifiReceiver.returnValue.put(json_data.getString("BSSID"), json_data.getString("Level"));
				}
				
				
				
				for(int[] x :List_of_Vectors){
					for(int y: x)
						Log.e("FORFORFORFOR","$$$$$$$$$$$$$$$$$$$$------>"+y);
					
					Log.e("FORFORFORFOR","-----------------NEXT--------------------->");
				
				}
				
				// //////////////////////////////////////////////////////////////////
				HttpClient httpClient2 = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(postDISTINCTvalueURL);
				JSONArray TIME = new JSONArray();
				JSONArray VECTOR = new JSONArray();
				JSONArray INTEGERary=  new JSONArray();
				
				
				int i = 0;
				for (int[] element : List_of_Vectors) {
					for(int x :element){
						INTEGERary.put(x);
						Log.e("DDSDSDSDSD","!!!!!!!!!!!!!!!"+x);
					}
					VECTOR.put(INTEGERary);
					INTEGERary=new JSONArray();
					TIME.put(time[i]);
					Log.e("AAAAAAAAA","!XXXXX"+time[i]);
					i++;
				}
				
				
				
				String VECTORstr = VECTOR.toString();
				String Timestr = TIME.toString();
				nameValuePairs.add(new BasicNameValuePair("vector", VECTORstr));
				nameValuePairs.add(new BasicNameValuePair("time", Timestr));
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

				HttpResponse response2 = httpClient2.execute(httpPost);// 嚙箴嚙碼嚙踝蕭嚙緘hp

				HttpEntity resEntity = response2.getEntity();

				if (resEntity != null) {
					String responseStr = EntityUtils.toString(resEntity).trim();
					Log.i(TAG, "Response: " + responseStr);

				}
				// //////////////////////////////////////////////////////////////////

			}

			for (int i = 0; i < PostDataAsyncTask.WifiReceiver.wifiList.size(); i++) {
				if (WifiReceiver.returnValue.containsKey(PostDataAsyncTask.WifiReceiver.wifiList.get(i).BSSID))
					WifiReceiver.returnValue.remove(PostDataAsyncTask.WifiReceiver.wifiList.get(i).BSSID);

			}

		} catch (JSONException e) {
			Log.i("XXXXXX", "JSON嚙踝蕭嚙瘤");
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		}

	}

	private void postText(int go_or_back,int PASSforSteps) throws ParseException, IOException {

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
			nameValuePairs.add(new BasicNameValuePair("steps", String
					.valueOf(PASSforSteps)));
			nameValuePairs.add(new BasicNameValuePair("distance", String
					.valueOf(WifiReceiver.mDistanceValue)));
			nameValuePairs.add(new BasicNameValuePair("speed", String
					.valueOf(WifiReceiver.mSpeedValue)));
			nameValuePairs.add(new BasicNameValuePair("direction", String
					.valueOf(WifiReceiver.degree[0])));
			nameValuePairs
					.add(new BasicNameValuePair("turn", WifiReceiver.turn));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
					HTTP.UTF_8));

			HttpResponse response = httpClient.execute(httpPost);// 嚙箴嚙碼嚙踝蕭嚙緘hp

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
