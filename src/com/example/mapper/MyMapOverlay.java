package com.example.mapper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class MyMapOverlay extends Overlay{

	int indicator;
	GeoPoint point; 
	Context cont;
	
	public MyMapOverlay(GeoPoint point,Context context, int indicator){
		this.indicator = indicator;
		this.point=point;
		this.cont=context;
		
	}
    @Override
    public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {  
        super.draw(canvas, mapView, shadow);  
        Point screenPts = new Point();  
        Bitmap bmp;
        mapView.getProjection().toPixels(point, screenPts);  
       
        if(indicator==1)
        	 bmp= BitmapFactory.decodeResource(cont.getResources(),R.drawable.greenpin);  
        else 
        	bmp = BitmapFactory.decodeResource(cont.getResources(),R.drawable.redpin); 
        canvas.drawBitmap(bmp, screenPts.x-bmp.getWidth()/2, screenPts.y-bmp.getHeight()/2, null);
        //canvas.drawBitmap(bmp, screenPts.x, screenPts.y-bmp.getHeight(), null);  
        
        super.draw(canvas,mapView,shadow);  

        return true;  
    }
    
    public GeoPoint getGeoPoint(){
    	return point;
    }

}
