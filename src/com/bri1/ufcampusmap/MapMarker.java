/*  UF Campus Map for Android
    Copyright (C) 2012 Bri1.Com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package com.bri1.ufcampusmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.content.Context;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class MapMarker extends Overlay {
	
	private GeoPoint location;
	private Bitmap markerBitmap;
	private int width, height;
	
	public MapMarker(GeoPoint location, Context context, int icon) {
		super();
		this.location = location;
		markerBitmap = BitmapFactory.decodeResource(context.getResources(), icon);
		height = markerBitmap.getHeight();
		width = markerBitmap.getWidth();
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		if(location == null)
			return;
		Point screenPoint = new Point();
		mapView.getProjection().toPixels(this.location, screenPoint);
		canvas.drawBitmap(markerBitmap, screenPoint.x-width/2, screenPoint.y-height/2, null);
	}
	
}