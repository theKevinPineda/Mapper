package com.example.mapper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class MainActivity extends MapActivity {
MapView mapView;
MapController mapController;
GeoPoint start,temp;
LocationListener locationListener;
LocationManager locationManager;
Projection projection;
List<Overlay> listOfOverlays;
List<Overlay> listOfOverlaysTemp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initialize();
	}

	
	
	public void initialize(){
		start=null;
		temp=null;
		mapView = (MapView)findViewById(R.id.map);
		listOfOverlays = new ArrayList<Overlay>();
		projection = mapView.getProjection();
		
		setButtons();
		setMapConfiguration();
		setController();

		mapController.animateTo(new GeoPoint((int)(10.35410*1E6),(int)(123.91145*1E6)));
	}
	public void setButtons(){
		((Button)findViewById(R.id.startButton)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setLocationListener();
				
			}
		});
		((Button)findViewById(R.id.stopButton)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				disableLocationListener();
				
			}
		});
		((Button)findViewById(R.id.clearButton)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				clear();
			}
		});
	}
	public void setLocationListener(){
		Toast.makeText(getBaseContext(), "Listener Started", Toast.LENGTH_SHORT).show();
		((Button)findViewById(R.id.stopButton)).setClickable(true);
		clear();
		locationListener = new MyLocationListener();
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 5, locationListener);
    	((Button)findViewById(R.id.startButton)).setClickable(false);
	}
	public void clear(){
		listOfOverlays.clear();
		mapView.invalidate();
	}
	public void disableLocationListener(){
		Toast.makeText(getBaseContext(), "Listener Stopped", Toast.LENGTH_SHORT).show();
		((Button)findViewById(R.id.stopButton)).setClickable(false);
		locationManager.removeUpdates(locationListener);
		((Button)findViewById(R.id.startButton)).setClickable(true);
	}
	
	public void setMapConfiguration(){
		mapView.setTraffic(true);
	}
	public void setController(){
		mapController = mapView.getController();
		mapController.setZoom(17);
		
	}
	public synchronized void projectTo(MyMapOverlay mapOverlay){
	    listOfOverlays = mapView.getOverlays();
        listOfOverlays.add(mapOverlay);   
        mapController.animateTo(mapOverlay.getGeoPoint());
        mapView.invalidate();
	}
	
	
	
	public synchronized void setInitialOverlay(){
		MyMapOverlay mapOverlay = new MyMapOverlay(start, this, 1);
		projectTo(mapOverlay);
	}
	public synchronized void setOverlay(){
		MyMapOverlay mapOverlay = new MyMapOverlay(temp, this, 2);
		projectTo(mapOverlay);
		callPathOverlay();
	}
	
	public synchronized void callPathOverlay(){
		
		RoutePathTask task = new RoutePathTask();
		task.execute();
	}
	
	
	
	class RoutePathTask extends AsyncTask<Void, Void, Boolean>{
		ProgressDialog pd;
		
		@Override
		protected void onPreExecute() {
	        mapView.invalidate(); 
			pd = new ProgressDialog(MainActivity.this);
			pd.setMessage("Loading Path...");
			pd.show();
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			Boolean result=false;
			JsonParsing json = new JsonParsing();
			try {
				synchronized(listOfOverlays){
				json.parsing(start, temp, mapView);

				start=temp;
				}
				result=true;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result=false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result=false;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result=false;
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result=false;
			}

			return result;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			pd.dismiss();
			if(result==false)
				Toast.makeText(getBaseContext(), "Faild to get Path, please secure a better internet connection", Toast.LENGTH_SHORT).show();
			
			
			mapView.invalidate();
		}
	}
	
	
	class MyLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			if(start==null){
				start = new GeoPoint((int)(location.getLatitude()*1E6),(int)(location.getLongitude()*1E6));
				setInitialOverlay();
			}
			else{
				temp = new GeoPoint((int)(location.getLatitude()*1E6),(int)(location.getLongitude()*1E6));
				setOverlay();
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			
			
		}

		@Override
		public void onProviderEnabled(String provider) {
		
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		
		return false;
	}

}
