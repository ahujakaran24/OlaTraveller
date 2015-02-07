package com.traveller.ola;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class AppPrefs_local {
	private static final String USER_PREFS = "USER_PREFS";
	private SharedPreferences appSharedPrefs;
	private SharedPreferences.Editor prefsEditor;
	private String username = "username";
	private String password="password";
	private String fromAddress = "fromAddress";
	private String toAddress = "toAddress";
	private String km = "km";
	private String money = "money";
	private String size = "size";
	private String current_user = "current_user";
	private String current_lat = "current_lat";
	private String current_long = "current_long";
	private String pick_address = "pick_address";
	private String drop_address = "drop_address";
	private String username_shipper = "username_shipper";
	private String distance = "distance";
	private String paying = "paying";
	private String nooflabor = "nooflabor";
	private String packag = "packag";
	private String latitude = "latitude";
	private String longitude = "longitude";
	private String isFirst = "isFirst";
	private String call ="call";
	private String year = "year";
	private String date = "date";
	private String month ="month";
	
	public AppPrefs_local(Context context){
		this.appSharedPrefs = context.getSharedPreferences(USER_PREFS, Activity.MODE_PRIVATE);
		this.prefsEditor = appSharedPrefs.edit();
	}
	public int getmonth()
	{
		return appSharedPrefs.getInt(month, 1);
	}
	public void setmonth(int num)
	{
		prefsEditor.putInt(month, num).commit();
	}
	public int getdate()
	{
		return appSharedPrefs.getInt(date, 1);
	}
	public void setdate(int num)
	{
		prefsEditor.putInt(date, num).commit();
	}
	public int getyear()
	{
		return appSharedPrefs.getInt(year, 1960);
	}
	public void setyear(int num)
	{
		prefsEditor.putInt(year, num).commit();
	}
	public int getIsFirst()
	{
		return appSharedPrefs.getInt(isFirst, 0);
	}
	public void setIsFirst(int num)
	{
		prefsEditor.putInt(isFirst, num).commit();
	}
	
	public String getuser() {
		return appSharedPrefs.getString(username, "");
	}

	public void setuser(String user) {
		prefsEditor.putString(username, user).commit();
	}
	public String getpass() {
		return appSharedPrefs.getString(password, "");
	}
	public void setpass(String pass) {
		prefsEditor.putString(password, pass).commit();
	}
	public String getfromAddress() {
		return appSharedPrefs.getString(fromAddress, "");
	}

	public void setfromAddress(String user) {
		prefsEditor.putString(fromAddress, user).commit();
	}
	public String gettoAddress() {
		return appSharedPrefs.getString(toAddress, "");
	}
	public void settoAddress(String pass) {
		prefsEditor.putString(toAddress, pass).commit();
	}
	public String getkm() {
		return appSharedPrefs.getString(km, "0");
	}

	public void setkm(String i) {
		prefsEditor.putString(km, i).commit();
	}
	public String getmoney() {
		return appSharedPrefs.getString(money, "0");
	}
	public void setmoney(String pass) {
		prefsEditor.putString(money, pass).commit();
	}
	public String getSize() {
		return appSharedPrefs.getString(size, "small");
	}
	public void setSize(String pass) {
		prefsEditor.putString(size, pass).commit();
	}
	public String getCurrentUser() {
		return appSharedPrefs.getString(current_user, "unknown");
	}
	public void setCurrentUser(String pass) {
		prefsEditor.putString(current_user, pass).commit();
	}
	public String getlat() {
		return appSharedPrefs.getString(current_lat, "0");
	}
	public void setlat(String pass) {
		prefsEditor.putString(current_lat, pass).commit();
	}
	public String getlong() {
		return appSharedPrefs.getString(current_long, "0");
	}
	public void setlong(String pass) {
		prefsEditor.putString(current_long, pass).commit();
	}
	public String getpick() {
		return appSharedPrefs.getString(pick_address, "Unknown");
	}

	public void setpick(String pick) {
		prefsEditor.putString(pick_address, pick).commit();
	}
	public String getcall() {
		return appSharedPrefs.getString(call, "");
	}

	public void setcall(String pick) {
		prefsEditor.putString(call, pick).commit();
	}
	public String getdrop() {
		return appSharedPrefs.getString(drop_address, "Unknown");
	}

	public void setdrop(String drop) {
		prefsEditor.putString(drop_address, drop).commit();
	}
	public String getusername() {
		return appSharedPrefs.getString(username_shipper, "Consignment not selected");
	}

	public void setusername(String user) {
		prefsEditor.putString(username_shipper, user).commit();
	}
	public String getdistance() {
		return appSharedPrefs.getString(distance, "Unknown");
	}

	public void setdistance(String dis) {
		prefsEditor.putString(distance, dis).commit();
	}
	public String getpaying() {
		return appSharedPrefs.getString(paying, "Unknown");
	}

	public void setpaying(String pay) {
		prefsEditor.putString(paying, pay).commit();
	}
	public String getnoofLabor() {
		return appSharedPrefs.getString(nooflabor, "Unknown");
	}

	public void setoofLabor(String labor) {
		prefsEditor.putString(nooflabor, labor).commit();
	}
	public String getpackage() {
		return appSharedPrefs.getString(packag, "Unknown");
	}

	public void setpackage(String pack) {
		prefsEditor.putString(packag, pack).commit();
	}
	
	public String getlatitude() {
		return appSharedPrefs.getString(latitude, "0");
	}

	public void setlatitude(String lat) {
		prefsEditor.putString(latitude, lat).commit();
	}
	public String getlongitude() {
		return appSharedPrefs.getString(longitude, "0");
	}

	public void setlongitude(String lng) {
		prefsEditor.putString(longitude, lng).commit();
	}
	
}

