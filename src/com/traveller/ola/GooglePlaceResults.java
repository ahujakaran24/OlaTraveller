package com.traveller.ola;

import org.json.JSONObject;

public class GooglePlaceResults 
{
   JSONObject geo;
   String icon;
   String vicinity;
   String name;
   
   public GooglePlaceResults(JSONObject geo, String icon, String vicinity, String name) 
   {
	   this.geo =geo;
	   this.icon =icon;
	   this.vicinity =vicinity;
	   this.name=name;	   
   }
   
   public JSONObject getgeo()
   {
	   return geo;
   }
   public String getvicinity()
   {
	   return vicinity;
   }
   public String geticon()
   {
	   return icon;
   }
   public String getname()
   {
	   return name;
   }
}
