package com.traveller.ola;


import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.location.GpsSatellite;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

//import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;


 
public class CustomAdapter extends BaseAdapter

{   
	
	private Context context;
	ArrayList<GooglePlaceResults> al;
	GPSTracker g;
	
	private static LayoutInflater inflater=null;
	public CustomAdapter(Context mainActivity,ArrayList<GooglePlaceResults> al, GPSTracker g )
	{
		context=mainActivity;
		inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.al=al;
		this.g=g;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return al.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public class Holder
	{
		TextView tv;
		ImageView img;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 

	{
		// TODO Auto-generated method stub
		Holder holder=new Holder();
		View rowView;       
		rowView = inflater.inflate(R.layout.list_layout, null);
		
		holder.tv=(TextView) rowView.findViewById(R.id.txt);         
		holder.img=(ImageView) rowView.findViewById(R.id.flag);  
		
		holder.tv.setText(al.get(position).getname());         //Name stored in obj
        UrlImageViewHelper.setUrlDrawable (holder.img, al.get(position).geticon());        //link stored in obj  

        
		rowView.setOnClickListener(new OnClickListener() {            
			@Override
			public void onClick(View v) 

			{
				double d[] = new double[4];
				 d[0] = g.getLatitude();
				 d[1] = g.getLongitude();
				//double dest_lat = al.get(position).
				
				try {
					JSONObject geo = al.get(position).getgeo();
					JSONObject x = geo.getJSONObject("location");
					String lat = x.getString("lat");
					String lng = x.getString("lng");
					
					 d[2] = Double.parseDouble(lat);
					 d[3] = Double.parseDouble(lng);
					
				//Toast.makeText(context, String.valueOf(dest_lat)+" "+String.valueOf(dest_lng)+" "+String.valueOf(cur_lat), Toast.LENGTH_LONG).show();
					
					Intent i = new Intent(context,Places.class);
					i.putExtra("loc", d);
					context.startActivity(i);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
						
				
			}
		});   
		return rowView;
	}


}
