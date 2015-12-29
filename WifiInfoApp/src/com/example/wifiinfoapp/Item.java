package com.example.wifiinfoapp;

import java.io.Serializable;

/**
 * @建立資料Item的Class
 *
 */
public class Item implements Serializable {

	private long id;
	private String SSID;
	private String Level;
	private String BSSID;
	private boolean selected;

	public Item() {
		SSID = "";
		Level = "";
		BSSID = "";
	}

	public Item(String SSID,String Level,String BSSID) {
		this.SSID = SSID;
		this.Level=Level;
		this.BSSID=BSSID;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getSSID() {
		return this.SSID;
	}

	public void setSSID(String SSID) {
		this.SSID = SSID;
	}

	public String getLevel() {
		return Level;
	}

	public void setLevel(String level) {
		Level = level;
	}

	public String getBSSID() {
		return BSSID;
	}

	public void setBSSID(String bSSID) {
		BSSID = bSSID;
	}

	
	
	
	
}
