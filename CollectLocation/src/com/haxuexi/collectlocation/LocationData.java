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

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public Date getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(Date collectionDate) {
		this.collectionDate = collectionDate;
	}
}
