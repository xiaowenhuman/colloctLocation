package com.haxuexi.collectlocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MyLocationListener implements LocationListener {
	String TAG = "MyLocationListener";
	JSONArray jsonArray = new JSONArray();
	private static final int SAVE_DATE_LENGTH = 5;
	TrackServiceDelegate trackServiceDelegate;

	public MyLocationListener(TrackServiceDelegate trackServiceDelegate) {
		 this.trackServiceDelegate = trackServiceDelegate;
	}
	 
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.v(TAG, "StatusChanged : " + provider + status);
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.v(TAG, "ProviderEnabled : " + provider);
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.v(TAG, "ProviderDisabled : " + provider);
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.v(TAG,
				"��γ�ȣ�" + location.getLatitude() + "   "
						+ location.getLongitude());
/*		Toast.makeText(MainActivity.this,
				"λ�øı���::::::::::::" + jsonArray.length(), 3000).show();*/
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("latitude", location.getLatitude());
			jsonObject.put("longitude", location.getLongitude());
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			jsonObject.put("collectionDate", sdf.format(date));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		jsonArray.put(jsonObject);
		if (jsonArray.length() >= SAVE_DATE_LENGTH ) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					((TrackService)trackServiceDelegate).test(""+jsonArray.length());
					postDataToNetwork();
				}
			}).start();

		}

	}
	
	private void postDataToNetwork() {
		String jsonStr = jsonArray.toString();
		System.out.println("�ϴ����" + jsonStr);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("userName", jsonStr);
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://192.168.1.100:8080/MyTrack/testAndroid.do");
		//���Ϊget������ݣ� �������ʹ��HttpGet
		
		List<BasicNameValuePair> postData = new ArrayList<BasicNameValuePair>();
		for(Map.Entry<String, String> entry : map.entrySet()){
			postData.add(new BasicNameValuePair(entry.getKey(),
					entry.getValue()));
		}
		
		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
					postData, HTTP.UTF_8);
			post.setEntity(entity);
			
			HttpResponse response = httpClient.execute(post);
			
			HttpEntity httpEntity = response.getEntity();
			InputStream is = httpEntity.getContent();
			
			StringBuffer sb = new StringBuffer();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			jsonArray = new JSONArray();
			System.out.println(sb.toString());
			Log.v(TAG, sb.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
