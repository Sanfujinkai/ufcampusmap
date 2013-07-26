package com.bri1.ufcampusmap;

/*
 * UF Campus Map for Android - Mk.II
 * Copyright (c) 2013 Brian Nezvadovitz
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.Locale;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bri1.ufcampusmap.contentprovider.BuildingContentProvider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class CampusMapFragment extends SupportMapFragment {

	static final LatLng CAMPUS = new LatLng(29.642, -82.357);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

		final GoogleMap map = getMap();
		if (null == map) {
			return view;
		}

		map.setMyLocationEnabled(true);

		// Check if empty, display message to user
		if (UFCMApplication.selectedBuildings.isEmpty()) {
			Toast.makeText(getActivity(), getResources().getString(R.string.toast_empty), Toast.LENGTH_SHORT).show();
			// Move the camera to campus and zoom in instantly
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(CAMPUS, 14));
		}

		LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

		// Place pins
		Marker marker = null;
		for (Long id : UFCMApplication.selectedBuildings) {
			android.util.Log.i(UFCMApplication.LOG_TAG, "Enumerated ID: " + id);
			Uri uri = Uri.withAppendedPath(BuildingContentProvider.CONTENT_URI,
					id.toString());
			final String[] projection = { BuildingTable.COLUMN_CNAME, BuildingTable.COLUMN_ABBR, BuildingTable.COLUMN_LAT, BuildingTable.COLUMN_LNG }; 
			Cursor c = getActivity().getContentResolver().query(uri, projection, null, null, null);
			if (c.moveToFirst() && c.getCount() == 1) {
				LatLng p = new LatLng(Double.parseDouble(c.getString(2)), Double.parseDouble(c.getString(3)));
				String abbrev = c.getString(1);
				if (abbrev.length() > 1 && !abbrev.toUpperCase(Locale.US).equals("NONE"))
					marker = map.addMarker(new MarkerOptions().title(c.getString(0)).snippet(abbrev).position(p));
				else
					marker = map.addMarker(new MarkerOptions().title(c.getString(0)).position(p));
				boundsBuilder.include(p);
			}
			c.close();
		}

		if (UFCMApplication.selectedBuildings.size() >= 2) {
			// only if 2 or more points on map
			final LatLngBounds bounds = boundsBuilder.build();
			map.setOnCameraChangeListener(new OnCameraChangeListener() {
				@Override
				public void onCameraChange(CameraPosition position) {
					// Zoom to show all pins
					final int padding = 85; // pixels
					map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
					// Remove listener to prevent position reset on camera move
					map.setOnCameraChangeListener(null);
				}
			});
		} else if (null != marker) {
			// only if 1 point on map
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 14));
			marker.showInfoWindow();
		}

		return view;
	}

}