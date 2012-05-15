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

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationHandler {

	private UFCMActivity context;
	private LocationManager lm;
	private boolean gpsEnabled = false;
	private boolean networkEnabled = false;
	
	public LocationHandler(UFCMActivity context) throws NoProvidersException, NoGpsException {
		this.context = context;
		lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
		// Exceptions will be thrown if the provider is not permitted
		try {
			gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch(Exception ex) {}
		try {
			networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch(Exception ex) {}
		
		// GPS is required for this program (network location only is not sufficient)
		if(!gpsEnabled)
			throw new NoGpsException();
		
		// Start up the network listener first, since it is the fastest
		if(networkEnabled)
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListenerNetwork);
		// Otherwise fall back to the GPS listener
		else if(gpsEnabled)
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListenerGps); // 1000ms interval, 1m distance
		// If neither provider is available, then we cannot continue
		else
			throw new NoProvidersException();
		
	}
	
	LocationListener locationListenerGps = new LocationListener() {
		public void onLocationChanged(Location loc) {
			context.newUserLocation(loc);
		}
		public void onProviderDisabled(String provider) {}
		public void onProviderEnabled(String provider) {}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	};

	LocationListener locationListenerNetwork = new LocationListener() {
		public void onLocationChanged(Location loc) {
			context.newUserLocation(loc);
			// Once we get a network Location fix, switch over to GPS provider (if available) for future updates
			if(gpsEnabled) {
				lm.removeUpdates(this);
				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListenerGps);
			}
		}
		public void onProviderDisabled(String provider) {}
		public void onProviderEnabled(String provider) {}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	};
	
	public void stop() {
		lm.removeUpdates(locationListenerGps);
		lm.removeUpdates(locationListenerNetwork);
	}
	
	public static class NoProvidersException extends Exception {
		private static final long serialVersionUID = 5714734215568938310L;
	}
	
	public static class NoGpsException extends Exception {
		private static final long serialVersionUID = -5516658194578849379L;
	}
	
}
