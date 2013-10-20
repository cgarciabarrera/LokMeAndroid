package com.cgb.mylocation;

public class Punto {
	
	/* PROPIEDADES */
	private double latitude=0;
	private double longitude=0;
	private float accuracy=0;
	private String provider="";
	private long timeFix=0;
	private float speed=0;
	private double altitude=0;
	private double bearing=0;
	
	
	public float getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public long getTimeFix() {
		return timeFix;
	}
	public void setTimeFix(long timeFix) {
		this.timeFix = timeFix;
	}
	public float getSpeed() {
		return speed;
	}
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	public double getAltitude() {
		return altitude;
	}
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	public double getBearing() {
		return bearing;
	}
	public void setBearing(double bearing) {
		this.bearing = bearing;
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
	

	
}