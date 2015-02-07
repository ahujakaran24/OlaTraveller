package com.traveller.ola;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Places extends FragmentActivity implements OnMapClickListener,OnMarkerClickListener,ConnectionCallbacks,OnConnectionFailedListener,LocationListener,OnMyLocationButtonClickListener,OnMarkerDragListener  {

	//There are many listeners implemented.
	private GoogleMap mMap;
	private String display="GPS is off";
	private LocationManager mLocationManager;
	Button size;
	private Marker mPick;
	private Marker mDrop;
	private double cur_lat =0,cur_lng=0,dest_lat=0,dest_lng=0; // latitude and longitude variables
	private double d[]=new double[4];
	private LocationClient mLocationClient;
	private Location mLocation;
	private String currentPickAddress;
	private String currentDropAddress;
	AppPrefs_local appPrefs;
	private TextView distance,money;
	private final CharSequence[] list={"1-2","2-4","4-9","10+"};
	private Button cab;
	private TextView pick,drop;
	// These settings are the same as the settings for the map. They will in fact give you updates
	// at the maximal rates currently possible.
	private static final LocationRequest REQUEST = LocationRequest.create().setInterval(100000).setFastestInterval(16).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_places);
		pick=(TextView)findViewById(R.id.pickup);
		drop=(TextView)findViewById(R.id.dropto);
		Context context = getApplicationContext();
		appPrefs = new AppPrefs_local(context); //Initializeg appPreferences
		size=(Button)findViewById(R.id.size);
		size.setText(appPrefs.getSize()); //setting size button text
		distance=(TextView)findViewById(R.id.distance);
		money=(TextView)findViewById(R.id.money);
		mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); //location manager object int.
		mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); 		// location object int.
		setUpMapIfNeeded(); // initialize the map if needed to avoid NPE in the following lines
		
		
		cab=(Button)findViewById(R.id.cabbutton);
		
		cab.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				//OLA API WOULD BE CALLED HERE. ASYNC TASK OR INTENTSERVICE. JSON/XML DATA PARSING LIKE GOOGLE API
				
				Toast.makeText(getApplicationContext(), "OLA API FOR CAB AVAIL / SEND LAT LONG WHEN CALLING", Toast.LENGTH_LONG).show();
				
			}
		});

		Intent i =getIntent();
		d=i.getDoubleArrayExtra("loc");
		cur_lat=d[0];
		cur_lng=d[1];
		dest_lat=d[2];
		dest_lng=d[3];
		try {
			currentPickAddress=latlongToAddress(cur_lat, cur_lng); // getting the current address of user here.
			//Toast.makeText(getApplicationContext(), String.valueOf(lat), Toast.LENGTH_LONG).show();
			currentDropAddress=latlongToAddress(dest_lat, dest_lng); // getting a nearby location to display as drop. can be change by the user in map.
			drop.setText(currentDropAddress);
			pick.setText(currentPickAddress);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mMap.setInfoWindowAdapter(new PopupAdapter());
		mPick=mMap.addMarker(new MarkerOptions().position(new LatLng(cur_lat, cur_lng)).title(currentPickAddress).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).draggable(true)); 		// adding a pick marker here
		mDrop=mMap.addMarker(new MarkerOptions().position(new LatLng(dest_lat, dest_lng)).title(currentDropAddress).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).draggable(true));  	// adding a drop marker here
		mPick.showInfoWindow();
		updatetext();
		CameraPosition cameraPosition = new CameraPosition.Builder()
		.target(new LatLng(cur_lat,cur_lng))      // Sets the center of the map to Mountain View
		.zoom(15)                   // Sets the zoom
		.bearing(0)                // Sets the orientation of the camera to north
		.tilt(30)                   // Sets the tilt of the camera to 30 degrees
		.build();                   // Creates a CameraPosition from the builder
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition)); // Calling this function so whenever user opens activity, it will get focused 
		mMap.getUiSettings().setZoomControlsEnabled(false); //Hide zoom buttons
		Toast.makeText(getApplicationContext(), "Click to set destination. Drag markers to change address!", Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		setUpMapIfNeeded();
		setUpLocationClientIfNeeded();
		mLocationClient.connect();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}
	}

	private void setUpMapIfNeeded()  // We initialize the map only if required.
	{
		// Do a null check to confirm that we have not already instantiated the map.
		if (mMap == null)
		{
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
					.getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) 
			{
				mMap.setMyLocationEnabled(true);
				mMap.setOnMyLocationButtonClickListener(this);
				mMap.setOnMarkerDragListener(this);
				mMap.setOnMarkerClickListener(this);
				mMap.setOnMapClickListener(this);
			}
		}
	}


	private void setUpLocationClientIfNeeded() { 
		if (mLocationClient == null) 
		{
			mLocationClient = new LocationClient(
					getApplicationContext(),
					this,  // ConnectionCallbacks
					this); // OnConnectionFailedListener
		}
	}

	/*** I have created a lot of listeners below, but they dont do anything. They are here if we need them in future. However, to optimize the code, they will be removed in the final version*/
	public void movetonextActivity() 
	{

	}

	@Override
	public void onLocationChanged(Location location) {
	}
	@Override
	public void onConnected(Bundle connectionHint) {
		mLocationClient.requestLocationUpdates( REQUEST, this);  // LocationListener
	}
	@Override
	public void onDisconnected()
	{
		// Do nothing
	}
	@Override
	public void onConnectionFailed(ConnectionResult result)
	{
		// Do nothing
	}
	@Override
	public boolean onMyLocationButtonClick()
	{
		Toast.makeText(this, "Your location", Toast.LENGTH_SHORT).show();
		// Return false so that we don't consume the event and the default behavior still occurs
		// (the camera animates to the user's current position).
		//	mMap.animateCamera( CameraUpdateFactory.zoomTo( 17.0f ) );   
		return false;
	}

	@Override
	public void onMarkerDrag(Marker marker) { // as marker is dragged, we change the distance

		updatetext();

	}

	@Override
	public void onMarkerDragEnd(Marker marker) 
	{
		// TODO Auto-generated method stub
		LatLng dragPosition = marker.getPosition();
		double dragLat = dragPosition.latitude;
		double dragLong = dragPosition.longitude;
		try {
			display=latlongToAddress(dragLat, dragLong);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//to.setText(String.valueOf(dragLat));
		marker.setTitle(display);
		marker.showInfoWindow();

		try {
			if(marker==mPick)
			{
				currentPickAddress=latlongToAddress(dragLat, dragLong); // getting the current address of user here.
				pick.setText(currentPickAddress);
			}
			else
			{
				currentDropAddress=latlongToAddress(dragLat, dragLong); // getting a nearby location to display as drop. can be change by the user in map.
				drop.setText(currentDropAddress);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Toast.makeText(getApplicationContext(),"updated", Toast.LENGTH_LONG).show();
	}
	@Override
	public void onMarkerDragStart(Marker marker) {
		//Dont need this function.
	}
	public String latlongToAddress(double lat, double lng) throws IOException  // A function to take arguments of lat long and return address.
	{
		String com_adr= "unknown"; 
		Geocoder geocoder;
		List<Address> addresses;
		geocoder = new Geocoder(this, Locale.getDefault());
		addresses = geocoder.getFromLocation(lat, lng, 1);
		if(addresses!=null && addresses.size()>0)
		{
			com_adr="";
			String address = addresses.get(0).getAddressLine(0);
			com_adr=com_adr+address;
			String city = addresses.get(0).getAddressLine(1);
			com_adr=com_adr+" "+city;
			String country = addresses.get(0).getAddressLine(2);
			com_adr=com_adr+" "+country;
		}
		return com_adr;
	}

	@Override
	public boolean onMarkerClick(Marker marker) 
	{
		return false;
	}

	public double CalculationByDistance(LatLng StartP, LatLng EndP) 
	{  
		// This formula will calculate distance between two markers
		int Radius=6371;//radius of earth in Km         
		double lat1 = StartP.latitude;
		double lat2 = EndP.latitude;
		double lon1 = StartP.longitude;
		double lon2 = EndP.longitude;
		double dLat = Math.toRadians(lat2-lat1);
		double dLon = Math.toRadians(lon2-lon1);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
				Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
				Math.sin(dLon/2) * Math.sin(dLon/2);
		double c = 2 * Math.asin(Math.sqrt(a));
		double valueResult= Radius*c;
		double km=valueResult/1;
		DecimalFormat newFormat = new DecimalFormat("####");
		int kmInDec =  Integer.valueOf(newFormat.format(km));
		double meter=valueResult%1000;
		int  meterInDec= Integer.valueOf(newFormat.format(meter));
		Log.i("Radius Value",""+valueResult+"   KM  "+kmInDec+" Meter   "+meterInDec);
		return Radius * c;
	}
	public static double round(double value, int places) {  //Distance is tooo accurate. This will round off the double value.
		if (places < 0) throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
	class PopupAdapter implements InfoWindowAdapter {  // Custom adapter for the information above marker

		private final View window = getLayoutInflater().inflate(R.layout.custom_bubble_map, null);
		@Override
		public View getInfoWindow(Marker marker) {
			String title = marker.getTitle();
			TextView txtTitle = ((TextView) window.findViewById(R.id.content));
			if (title != null) {
				// Spannable string allows us to edit the formatting of the text.
				SpannableString titleText = new SpannableString(title);
				txtTitle.setText(titleText);
			} else {
				txtTitle.setText("");
			}
			return window;
		}
		@Override
		public View getInfoContents(Marker marker) {
			//this method is not called if getInfoWindow(Marker) does not return null
			return null;
		}
	}
	@Override
	public void onMapClick(LatLng point) { // setting drop
		mDrop.remove();
		String address="";
		try {
			address = latlongToAddress(point.latitude, point.longitude);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mDrop=mMap.addMarker(new MarkerOptions().position(new LatLng(point.latitude, point.longitude)).title(address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).draggable(true));
		mDrop.showInfoWindow();
		updatetext();
	}
	private void updatetext() //updates distance and money
	{
		double dResults = CalculationByDistance(mPick.getPosition(), mDrop.getPosition());
		dResults=round(dResults,2);
		double dMoney=dResults*10; // for mvp assuming 10rs/km
		distance.setText(String.valueOf(dResults) + "KM");
		money.setText(String.valueOf(dMoney)+"Rs");

	}

	public void getsize(View v) //Called when Size is clicked
	{

		AlertDialog.Builder builder=new AlertDialog.Builder(Places.this)
		.setTitle("Choose Number Of People")
		.setItems(list, new DialogInterface.OnClickListener() { 
			@Override 
			public void onClick(DialogInterface dialog, int which)
			{ 
				switch(which)
				{
				case 0:
					appPrefs.setSize(list[which].toString());
					size.setText(list[which]);
					break;
				case 1:
					appPrefs.setSize(list[which].toString());
					size.setText(list[which]);
					break;
				case 2: 
					appPrefs.setSize(list[which].toString());
					size.setText(list[which]);
					break;
				case 3:
					appPrefs.setSize(list[which].toString());
					size.setText(list[which]); 
					break;
				default: break;
				}
			} 
		});
		AlertDialog alertdialog=builder.create(); 
		alertdialog.show();
	}
	public void gotonext(View v)
	{
		appPrefs.setfromAddress(mPick.getTitle());
		appPrefs.settoAddress(mDrop.getTitle());
		appPrefs.setkm(distance.getText().toString());
		appPrefs.setmoney(money.getText().toString());
		appPrefs.setlat(String.valueOf(mPick.getPosition().latitude));
		appPrefs.setlong(String.valueOf(mPick.getPosition().longitude));

		if(mPick.getTitle().equals("unknown") || mDrop.getTitle().equals("unknown"))
		{
			Toast.makeText(getApplicationContext(), "Address can not be Unknown", Toast.LENGTH_LONG).show();
		}
		else
		{
			//Intent i = new Intent(getApplicationContext(),Post_checkout.class);
			//startActivity(i);
		}
	}
}

