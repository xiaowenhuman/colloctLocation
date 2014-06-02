package com.haxuexi.collectlocation;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class TrackService extends Service implements TrackServiceDelegate{
	String TAG = "TrackService";
	private LocationManager locationManager = null;	
	protected MyLocationListener locationListener = new MyLocationListener(this);
    /**
     * Receives messages from activity.
     */
    private final IBinder mBinder = new TrackBinder();
    
	@Override
	public IBinder onBind(Intent intent) {
		Log.v(TAG, "onBind");
		return mBinder;
	}


	@Override
	public void onCreate() {
		
		String serviceName = Context.LOCATION_SERVICE;
		// ��ȡλ�ù������
		locationManager = (LocationManager) getSystemService(serviceName);
		registLocationListener();
		//--------------------插入站点数据结束
		super.onCreate();
		Log.v(TAG, "onCreate");
		
		
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.v(TAG, "onUnbind");
		locationManager.removeUpdates(locationListener);
		return super.onUnbind(intent);
	}
    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class TrackBinder extends Binder {
        TrackService getService() {
            return TrackService.this;
        }
    }
    public interface ICallback {
        public void refresh(String displaystring);
    }
    
    private ICallback mCallback;

    public void registerCallback(ICallback cb) {
        mCallback = cb;
    }
    public void test(String backString){
    	mCallback.refresh(backString);
    }

	@Override	
	public void getDataFromEvent(int eventId) {
	}
	
	private void registLocationListener() {
		LocationProvider gpslLocationProvider =  locationManager.getProvider(LocationManager.GPS_PROVIDER);
		   String providerName = null;
//        if (gpslLocationProvider != null) {
//        	providerName = LocationManager.GPS_PROVIDER;
//		} else {
		        // ���ҵ�������Ϣ  
		        Criteria criteria = new Criteria();  
		        criteria.setAccuracy(Criteria.ACCURACY_FINE); // �߾���  
		        criteria.setAltitudeRequired(false);  
		        criteria.setBearingRequired(false);  
		        criteria.setCostAllowed(true);  
		        criteria.setPowerRequirement(Criteria.POWER_LOW); // �͹���  
		  
		        providerName = locationManager.getBestProvider(criteria, true); // ��ȡGPS��Ϣ  
//		}
		    
		    Location location = locationManager.getLastKnownLocation(providerName);
		    
		    Log.v(TAG, "providerName is :" + providerName);
		    if (location != null) {
				Log.v(TAG, "��γ�ȣ�" + location.getLatitude() + "   "
						+ location.getLongitude());
				Log.v(TAG, "���ڿ�ʼ�ɼ���Ϣ");
			}
			
		    
//        locationManager.requestLocationUpdates(providerName, 100 * 1000, 500,  
//                locationListener);
		    
		    locationManager.requestLocationUpdates(providerName, 2 * 1000, 0,  
		            locationListener);
	}
}
