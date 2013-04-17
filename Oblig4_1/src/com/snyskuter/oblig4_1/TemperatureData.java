package com.snyskuter.oblig4_1;

public class TemperatureData {
	private String place;
	private String url;
	private String temperature;
	
	public String getPlace() { return place; }
	public String getUrl() { return url; }
	public String getTemperature() { return temperature; }
	
	public TemperatureData(String place, String url, String temperature) {
		this.place = place;
		this.url = url;
		this.temperature = temperature;
	}
	
	public String toString() {
		return place;
	}
}
