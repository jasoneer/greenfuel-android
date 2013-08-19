package com.isotix.nufuel;

import java.util.Iterator;
import java.util.ArrayList;

import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.maps.MapView;
import com.google.android.maps.Projection;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;

public class RouteOverlay extends Overlay
{
	private Paint route,
				  point;

	public RouteOverlay()
	{
		route = new Paint();
		route.setARGB(255, 255, 255, 0);
		route.setStyle(Paint.Style.STROKE);
        route.setStrokeJoin(Paint.Join.ROUND);
        route.setStrokeCap(Paint.Cap.ROUND);
        route.setStrokeWidth(10);

		point = new Paint();
		point.setARGB(255, 255, 150, 0);
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		super.draw(canvas, mapView, shadow);
		if(!shadow) {
			Projection projection = App.CurrentActivity.getProjection();
			synchronized(Search.RouteFixes) {
				Iterator<double[]> it = Search.RouteFixes.iterator();
				
				if(it.hasNext()) {
					//long currentTime = System.currentTimeMillis();
				
					double[] startLoc = it.next();
					GeoPoint startGeo = new GeoPoint((int)(startLoc[0] * 1E6),
													 (int)(startLoc[1] * 1E6));
					Point startPnt = projection.toPixels(startGeo, null);
					Point begPoint = startPnt;
												 
					//long firstTime = startLoc.TimeStamp();
					
					while(it.hasNext()) {
						double[] stopLoc = it.next();
						GeoPoint stopGeo = new GeoPoint((int)(stopLoc[0] * 1E6),
														(int)(stopLoc[1] * 1E6));
						Point stopPnt = projection.toPixels(stopGeo, null);
						//int color = (int)(255.0 * Math.min(Math.max((stopLoc.TimeStamp()-firstTime) * 1E-3 * 0.005, 0), 1));
						//route.setARGB(255, color, color, 0);
					
						canvas.drawLine(startPnt.x, startPnt.y, stopPnt.x, stopPnt.y, route);
						
						startLoc = stopLoc;
						startPnt = stopPnt;
					}
					canvas.drawCircle(begPoint.x, begPoint.y, 10, point);
					canvas.drawCircle(startPnt.x, startPnt.y, 10, point);
				}
			}
		}
	}
}
