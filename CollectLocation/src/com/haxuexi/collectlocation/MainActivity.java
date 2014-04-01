package com.haxuexi.collectlocation;


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
	
	private static final String TAG = "MainActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		String serviceName = Context.LOCATION_SERVICE;
		// ��ȡλ�ù������
		locationManager = (LocationManager) getSystemService(serviceName); 
       
		openGPSSettings();
		
		collectLocationBtnButton = (Button)findViewById(R.id.collectLocation);
		stopCollectLocationBtn = (Button)findViewById(R.id.stopCollectLocation);
		displayTextView = (TextView)findViewById(R.id.textView);
		collectLocationBtnButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				

		       LocationProvider gpslLocationProvider =  locationManager.getProvider(LocationManager.GPS_PROVIDER);
		       String providerName = null;
		        if (gpslLocationProvider != null) {
		        	providerName = LocationManager.GPS_PROVIDER;
				} else {
			        // ���ҵ�������Ϣ  
			        Criteria criteria = new Criteria();  
			        criteria.setAccuracy(Criteria.ACCURACY_FINE); // �߾���  
			        criteria.setAltitudeRequired(false);  
			        criteria.setBearingRequired(false);  
			        criteria.setCostAllowed(true);  
			        criteria.setPowerRequirement(Criteria.POWER_LOW); // �͹���  
			  
			        providerName = locationManager.getBestProvider(criteria, true); // ��ȡGPS��Ϣ  
				}

		        Location location = locationManager.getLastKnownLocation(providerName);
		        
		        
		        if (location != null) {
					Log.v(TAG, "��γ�ȣ�" + location.getLatitude() + "   "
							+ location.getLongitude());
					Log.v(TAG, "���ڿ�ʼ�ɼ���Ϣ");
					displayTextView.setText("�ı�����");
				}
				
		        
//		        locationManager.requestLocationUpdates(providerName, 100 * 1000, 500,  
//		                locationListener);
		        
		        locationManager.requestLocationUpdates(providerName, 1 * 1000, 1,  
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
			Toast.makeText(this, "GPSģ������", Toast.LENGTH_SHORT).show();
			return;
		}

		Toast.makeText(this, "�뿪��GPS��", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
		startActivityForResult(intent, 0); // ��Ϊ������ɺ󷵻ص���ȡ����

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
			 Log.v(TAG, "��γ�ȣ�" + location.getLatitude() + "   " + location.getLongitude());
			 displayTextView.setText("��γ�ȣ�" + location.getLatitude() + "   " + location.getLongitude());
			 Toast.makeText(MainActivity.this, "λ�øı���::::::::::::", 3000).show();
		}
	};
	private void showNotification() {
		// ����һ��NotificationManager������
		Context context=getBaseContext();
		NotificationManager notificationManager = (NotificationManager) 
			context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		
		// ����Notification�ĸ�������
		Notification notification = new Notification(R.drawable.ic_launcher,
				getString(R.string.back_app_name), System.currentTimeMillis());
		notification.flags |= Notification.FLAG_ONGOING_EVENT; // ����֪ͨ�ŵ�֪ͨ����"Ongoing"��"��������"����
		notification.flags |= Notification.FLAG_NO_CLEAR; // �����ڵ����֪ͨ���е�"���֪ͨ"�󣬴�֪ͨ�������������FLAG_ONGOING_EVENTһ��ʹ��
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		notification.defaults = Notification.DEFAULT_LIGHTS;
		notification.ledARGB = Color.BLUE;
		notification.ledOnMS = 5000;
				
		// ����֪ͨ���¼���Ϣ
		CharSequence contentTitle = getString(R.string.status_bar_string); // ֪ͨ������
		CharSequence contentText = "ameyume"; // ֪ͨ������
		Intent notificationIntent = new Intent(); // �����֪ͨ��Ҫ��ת��Activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); 
		notificationIntent.setClass(this, this.getClass());
		PendingIntent contentItent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		context.startActivity(notificationIntent);
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentItent);
		// ��Notification���ݸ�NotificationManager
		notificationManager.notify(0, notification);
	}
}
