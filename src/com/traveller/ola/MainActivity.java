package com.traveller.ola;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Button ola,quikbok;
	private GPSTracker gpstracker= new GPSTracker(this);
	private List<NameValuePair> params = new ArrayList<NameValuePair>(); //TO POST DATA
	private String vps_url = "http://198.12.152.47/~ahujakaran24/ola/nearby.php"; // Location where php script is querying data from Google API
	private ListView lv;

	private static final String TAG_GEOMETRY = "geometry";
	private static final String TAG_ICON = "icon";
	private static final String TAG_NAME = "name";
	private static final String TAG_VICINITY = "vicinity";

	private SeekBar skbar;
	private TextView km;
        


	private static final String TAG_STATUS = "status";
	private static final String TAG_RESULT = "results";  //Array of returned results by Google

   private int dis=50; //50 km is max radi of search for Google

	JSONArray results;
	ArrayList<GooglePlaceResults> data = new ArrayList<GooglePlaceResults>();  //Store results in object of Google Place Results

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
       
		Intent j = new Intent(this,InvisibleAct.class);
		startActivity(j);
		
		flipCard();  //animation flip just to attract user attention 
		
		
		ola = (Button)findViewById(R.id.olame);  // Ola start button
		quikbok = (Button)findViewById(R.id.quikbook);
		  //Tracker class obj
		lv=(ListView)findViewById(R.id.list);
		skbar=(SeekBar)findViewById(R.id.seekBar1);
		km=(TextView)findViewById(R.id.textView1);
		
		lv.setVisibility(View.INVISIBLE);
		skbar.setMax(50);
		skbar.setProgress(50);
		km.setText("50 km");
		
		quikbok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) 
			{
				quickbook();
			}
		});

		ola.setOnClickListener(new OnClickListener()   //Listener to ola
		{

			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0)  //Fetch current lat long
			{
				//  ola.setLayoutParams(new RelativeLayout.LayoutParams(10,10));
				//	gpstracker = new GPSTracker(getApplicationContext()); 
				startTheSearch();
			    

			}
		});
		
		skbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{       

		    @Override       
		    public void onStopTrackingTouch(SeekBar seekBar) 
		    {      
		        // TODO Auto-generated method stub      
		    }       

		    @Override       
		    public void onStartTrackingTouch(SeekBar seekBar)
		    {     
		        // TODO Auto-generated method stub      
		    }       

		    @Override       
		    public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser)
		    {     
		        // TODO Auto-generated method stub      
                dis=progress;
		        km.setText(progress + " km");

		    }       
		}); 
		
		flipCard(); 
	}

	@SuppressLint("NewApi")
	public void startTheSearch() 

	{
		if (gpstracker.canGetLocation()) //GET CURRENT GPS LAT LONG
		{
			gpstracker = new GPSTracker(this);
			params.add(new BasicNameValuePair("latitude", String.valueOf(gpstracker.latitude)));
			params.add(new BasicNameValuePair("longitude", String.valueOf(gpstracker.longitude)));
			params.add(new BasicNameValuePair("distance",String.valueOf(dis)));
			System.out.println(params.toString());
			new queryData().execute();
			ObjectAnimator oa = ObjectAnimator.ofFloat(ola, "translationY", 0, -500f);
			oa.setDuration(300);				
			oa.start();
			lv.setVisibility(View.VISIBLE);
			skbar.setVisibility(View.INVISIBLE);
			km.setVisibility(View.INVISIBLE);	
		}

		else
		{
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			ObjectAnimator oa = ObjectAnimator.ofFloat(ola, "translationY", 0, -500f);
			oa.setDuration(300);				
			oa.start();
			ObjectAnimator ob = ObjectAnimator.ofFloat(ola, "translationY", 0, 500f);
			ob.setDuration(300);				
			ob.start();
			gpstracker.showSettingsAlert();
			gpstracker = new GPSTracker(this);
		}
	}
	
	  

	@Override
	public void onResume() //When user comes back to activity after switching on the GPS
	{
		super.onResume();
		gpstracker=new GPSTracker(this);
	}

	
	      
	/*********Async task to query JSON Data from VPS --> Google Api******/

	public class queryData extends AsyncTask<Void, Void, JSONObject>
	{
		String status;

		@Override
		protected JSONObject doInBackground(Void... args0)  //Network operations
		{

			JSONParser jp= new JSONParser();    //JP object

			JSONObject json = jp.makeHttpRequest(vps_url, "POST", params);

			return json;
		}

		@Override
		protected void onPostExecute(JSONObject j)  //Back to Main thread
		{
			super.onPostExecute(j);		
			if(j!=null)    //If Json received and is !null
			{
				//	System.out.println(j);   //Debugging				
				try 
				{
					status=j.getString(TAG_STATUS);  //Get status of response

					if(status.equals("OK")) {DoYourMagic(j);}   //Go go go
					else if(status.equals("ZERO_RESULTS")) {Toast.makeText(getApplicationContext(), "Unfortunately there are no places to see near you ", Toast.LENGTH_LONG).show();}
					else {Toast.makeText(getApplicationContext(), "Something went terribly wrong with Google.. we are sorry", Toast.LENGTH_LONG).show();}	
				} 
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
			}
			else  //If JSon not received and is null
			{
				Toast.makeText(getApplicationContext(), "Check internet connection", Toast.LENGTH_LONG).show();
			}
		}
	}

	public void DoYourMagic(JSONObject j) //Now we have the json data 
	{

		
		Toast.makeText(getApplicationContext(),"Showing Nearby Places",Toast.LENGTH_LONG).show();

		try 
		{
			results=j.getJSONArray(TAG_RESULT);
			for(int i=0;i<results.length();i++)
			{
				if(results.getJSONObject(i).getJSONObject(TAG_GEOMETRY)!=null&&results.getJSONObject(i).getString(TAG_ICON)!=null&&results.getJSONObject(i).getString(TAG_VICINITY)!=null&&results.getJSONObject(i).getString(TAG_NAME)!=null)
				{
					GooglePlaceResults g= new GooglePlaceResults(results.getJSONObject(i).getJSONObject(TAG_GEOMETRY),results.getJSONObject(i).getString(TAG_ICON),results.getJSONObject(i).getString(TAG_VICINITY),results.getJSONObject(i).getString(TAG_NAME));
					data.add(g);
				}
			}
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}

		System.out.println(data.toString());
		//ola.setLayoutParams(new RelativeLayout.LayoutParams(100,100));
		CustomAdapter c = new CustomAdapter(MainActivity.this,data,gpstracker);
		lv.setAdapter(c);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event

		
			switch (item.getItemId()) 
			{
			
			case R.id.quick:
				
				  flipCard();
				
				return true;
				default: return super.onOptionsItemSelected(item);
			}
	}
	
	public void quickbook()
	{
		Intent i = new Intent(this,Places.class);
		double d[] = new double[4];
		d[0]=gpstracker.getLatitude();
		d[1]=gpstracker.getLongitude();
		d[2]=gpstracker.getLatitude()-.1; //to set drop a little away
		d[3]=gpstracker.getLongitude();
		i.putExtra("loc", d);
		this.startActivity(i);
	}
	
	

	private void flipCard()
	{
	    View rootLayout = findViewById(R.id.main_activity_root);
	    View cardFace = findViewById(R.id.card_front);
	    View cardBack = findViewById(R.id.card_back);

	    FlipAnimation flipAnimation = new FlipAnimation(cardFace, cardBack);

	    if (cardFace.getVisibility() == View.GONE)
	    {
	        flipAnimation.reverse();
	    }
	    rootLayout.startAnimation(flipAnimation);
	}

}
