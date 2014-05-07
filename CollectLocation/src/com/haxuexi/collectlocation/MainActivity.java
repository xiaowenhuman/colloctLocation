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

import android.support.v7.app.ActionBarActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings;

public class MainActivity extends ActionBarActivity {

	private Button collectLocationBtnButton = null;
	private Button stopCollectLocationBtn = null;

	private TextView displayTextView = null;
    private LocationManager locationManager = null;
    
    JSONArray jsonArray = new JSONArray();
    private static final String URL_PATH = "http://192.168.1.100:8080/Track/isLoginCheck.do";
    
    private static final int SAVE_DATE_LENGTH = 5;
	
	private static final String TAG = "MainActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		String serviceName = Context.LOCATION_SERVICE;
		// 获取位置管理服务
		locationManager = (LocationManager) getSystemService(serviceName); 
       
//		openGPSSettings();
		
		collectLocationBtnButton = (Button)findViewById(R.id.collectLocation);
		stopCollectLocationBtn = (Button)findViewById(R.id.stopCollectLocation);
		displayTextView = (TextView)findViewById(R.id.textView);
		collectLocationBtnButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				

		       LocationProvider gpslLocationProvider =  locationManager.getProvider(LocationManager.GPS_PROVIDER);
		       String providerName = null;
//		        if (gpslLocationProvider != null) {
//		        	providerName = LocationManager.GPS_PROVIDER;
//				} else {
			        // 查找到服务信息  
			        Criteria criteria = new Criteria();  
			        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度  
			        criteria.setAltitudeRequired(false);  
			        criteria.setBearingRequired(false);  
			        criteria.setCostAllowed(true);  
			        criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗  
			  
			        providerName = locationManager.getBestProvider(criteria, true); // 获取GPS信息  
//				}
			    
		        Location location = locationManager.getLastKnownLocation(providerName);
		        
		        Log.v(TAG, "providerName is :" + providerName);
		        if (location != null) {
					Log.v(TAG, "经纬度：" + location.getLatitude() + "   "
							+ location.getLongitude());
					Log.v(TAG, "现在开始采集信息");
					displayTextView.setText("改变文字");
				}
				
		        
//		        locationManager.requestLocationUpdates(providerName, 100 * 1000, 500,  
//		                locationListener);
		        
		        locationManager.requestLocationUpdates(providerName, 1 * 1000, 0,  
		                locationListener);
			};
		});
		
		
		stopCollectLocationBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				locationManager.removeUpdates(locationListener);
			}
		});
	}
	

	@Override
	protected void onStop() {
		super.onStop();
		
		showNotification();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		locationManager.removeUpdates(locationListener);
	}


	private void openGPSSettings() {
		LocationManager alm = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			Toast.makeText(this, "GPS模块正常", Toast.LENGTH_SHORT).show();
			return;
		}

		Toast.makeText(this, "请开启GPS！", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
		startActivityForResult(intent, 0); // 此为设置完成后返回到获取界面

	}
	
	LocationListener locationListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.v(TAG, "StatusChanged : " + provider + status); 
			displayTextView.setText("StatusChanged : " + provider + status);
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			 Log.v(TAG, "ProviderEnabled : " + provider); 
			 displayTextView.setText("ProviderEnabled : " + provider);
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			 Log.v(TAG, "ProviderDisabled : " + provider);
			 displayTextView.setText("ProviderDisabled : " + provider);
		}
		
		@Override
		public void onLocationChanged(Location location) {
			Log.v(TAG,
					"经纬度：" + location.getLatitude() + "   "
							+ location.getLongitude());
			displayTextView.setText("经纬度"+jsonArray.length() + "：" + location.getLatitude() + "   "
					+ location.getLongitude() );
			Toast.makeText(MainActivity.this,
					"位置改变了::::::::::::" + jsonArray.length(), 3000).show();
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
			if (jsonArray.length() >= SAVE_DATE_LENGTH) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						 postDataToNetwork();
					}
				}).start();
				
			}

		}
		
	};

	private void postDataToNetwork() {
		String jsonStr = jsonArray.toString();
		System.out.println("上传数据" + jsonStr);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("userName", jsonStr);
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://192.168.1.112:8080/Track/testAndroid.do");
		//如果为get请求数据， 这里可以使用HttpGet
		
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
	private void showNotification() {
		// 创建一个NotificationManager的引用
		Context context=getBaseContext();
		NotificationManager notificationManager = (NotificationManager) 
			context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		
		// 定义Notification的各种属性
		Notification notification = new Notification(R.drawable.ic_launcher,
				getString(R.string.back_app_name), System.currentTimeMillis());
		notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
		notification.flags |= Notification.FLAG_NO_CLEAR; // 表明在点击了通知栏中的"清除通知"后，此通知不清除，经常与FLAG_ONGOING_EVENT一起使用
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		notification.defaults = Notification.DEFAULT_LIGHTS;
		notification.ledARGB = Color.BLUE;
		notification.ledOnMS = 5000;
				
		// 设置通知的事件消息
		CharSequence contentTitle = getString(R.string.status_bar_string); // 通知栏标题
		CharSequence contentText = "ameyume"; // 通知栏内容
		Intent notificationIntent = new Intent(); // 点击该通知后要跳转的Activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); 
		notificationIntent.setClass(this, this.getClass());
		PendingIntent contentItent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		context.startActivity(notificationIntent);
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentItent);
		// 把Notification传递给NotificationManager
		notificationManager.notify(0, notification);
	}
}
