package com.haxuexi.collectlocation;

import com.haxuexi.utils.DistanceUtil;

import android.support.v7.app.ActionBarActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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
	Button distanceBtn = null;

	private TextView displayTextView = null;
    
    private TrackService mService;
    private static final int REFRESH_STRING = 1;
    
	
    private static final String URL_PATH = "http://192.168.1.100:8080/Track/isLoginCheck.do";
    
	
	private static final String TAG = "MainActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
//		openGPSSettings();
		
		collectLocationBtnButton = (Button)findViewById(R.id.collectLocation);
		stopCollectLocationBtn = (Button)findViewById(R.id.stopCollectLocation);
		displayTextView = (TextView)findViewById(R.id.textView);
		distanceBtn = (Button) findViewById(R.id.distance);
		collectLocationBtnButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
		       bindStepService();
			}
		});
		
		
		stopCollectLocationBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				unbindStepService();
			}
		});
		
		distanceBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				double distance = 0 ;
				double lon1=106.357011;
				double lat1 = 29.553152;
				double lon2 = 106.848563;
				double lat2 = 29.557174;
				distance = DistanceUtil.LantitudeLongitudeDist(lon1, lat1, lon2, lat2);
				distanceBtn.setText("" + distance);
			}
		});
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REFRESH_STRING:
				collectLocationBtnButton.setText(msg.obj.toString());
				break;
			default:
				super.handleMessage(msg);
			}

		}
	};

	@Override
	protected void onStop() {
		super.onStop();
		
		showNotification();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


	private void openGPSSettings() {
		LocationManager alm = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			Toast.makeText(this, "GPSģ����", Toast.LENGTH_SHORT).show();
			return;
		}

		Toast.makeText(this, "�뿪��GPS��", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
		startActivityForResult(intent, 0); // ��Ϊ������ɺ󷵻ص���ȡ����

	}
	
    
	private void bindStepService() {
		Log.i(TAG, "[SERVICE] Bind");
		bindService(new Intent(this, TrackService.class),
				mConnection, Context.BIND_AUTO_CREATE
						+ Context.BIND_DEBUG_UNBIND);
	}

    private void unbindStepService() {
        Log.i(TAG, "[SERVICE] Unbind");
        unbindService(mConnection);
    }
	
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
        	mService = ((TrackService.TrackBinder)service).getService();
        	 mService.registerCallback(mCallback);
            Log.v(TAG, "onServiceConnected");
        }

        public void onServiceDisconnected(ComponentName className) {
        	mService = null;
        	Log.v(TAG, "onServiceDisconnected");
        }
    };
    TrackService.ICallback mCallback = new TrackService.ICallback(){

		@Override
		public void refresh(String displaystring) {
			mHandler.sendMessage(mHandler.obtainMessage(REFRESH_STRING, 1,1,displaystring));
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
		notification.flags |= Notification.FLAG_NO_CLEAR; // �����ڵ����֪ͨ���е�"���֪ͨ"�󣬴�֪ͨ��������FLAG_ONGOING_EVENTһ��ʹ��
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
