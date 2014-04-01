package com.haxuexi.collectlocation;

import java.util.Date;

public class LocationData {
	
	double latitude;
	double longitude;
	Date collectionDate;
	
	public LocationData(double latitude, double longitude, Date collectionDate){
		this.latitude = latitude;
		this.longitude =  longitude;
		this.collectionDate = collectionDate;
	}
}
