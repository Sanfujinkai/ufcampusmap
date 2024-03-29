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

import android.view.MotionEvent;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class MapTouchOverlay extends Overlay {
	
	private MapTouchHandler mth;
	
	public MapTouchOverlay(MapTouchHandler mth) {
		this.mth = mth;
	}
	
	@Override public boolean onTouchEvent(MotionEvent event, MapView mv) {
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			mth.touch();
		}
		return false;
	}
	
	public static abstract class MapTouchHandler {
		public abstract void touch();
	}
	
}
