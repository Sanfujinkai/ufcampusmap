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

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class UFCMActivity extends MapActivity {

	private MapView mapView;
	private GeoPoint curLocation;
	private GeoPoint bldgLocation;
	private LocationHandler lh;
	private MapTouchOverlay mapTouchOverlay;

	private final static int DEFAULT_ZOOM_LEVEL = 15;
	private final static int PINNED_ZOOM_LEVEL = 19;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Set up the MapView
		mapView = (MapView) findViewById(R.id.map_view);
		mapView.setBuiltInZoomControls(true);
	}

	@Override
	public boolean onSearchRequested() {
		UFCMApplication.followUserLocation = true;
		Intent i = new Intent(this, BuildingListActivity.class);
		startActivity(i);
		return true;
	}

	private void updateMapOverlays(MapView mapView) {
		List<Overlay> overlayList = mapView.getOverlays();
		overlayList.clear();
		overlayList.add(new MapMarker(curLocation, this, R.drawable.ic_maps_indicator_current_position));
		overlayList.add(new MapMarker(bldgLocation, this, R.drawable.pin));
		overlayList.add(mapTouchOverlay);
		mapView.invalidate();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// Place a pin on the selected building, if there is one
		if(UFCMApplication.dbCurrentId != UFCMApplication.dbInvalidId) {
			newBldgLocation(BuildingTable.lookupSingleBuildingGeoPointById(this, UFCMApplication.dbCurrentId));
		}
		
		// Register to get location updates
		try {
			lh = new LocationHandler(this);
		} catch(Exception e) {
			gpsNotAvailable();
		}
		
		// Set up the overlays
		mapTouchOverlay = new MapTouchOverlay(new MapTouchOverlay.MapTouchHandler() {
			@Override
			public void touch() {
				UFCMApplication.followUserLocation = false;
			}
		});
		updateMapOverlays(mapView);
	}
	
	@Override
	protected void onPause() {
		// Stop location updates
		if(lh != null) lh.stop();
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		// Really stop location updates
		if(lh != null) lh.stop();
		super.onDestroy();
	}
	
	private void gpsNotAvailable() {
		Toast.makeText(this, getString(R.string.gps_required), Toast.LENGTH_LONG).show();
		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(intent);
		finish();
	}

	public void newUserLocation(Location loc) {
		if(loc != null) {
			curLocation = new GeoPoint((int)(loc.getLatitude() * 1E6), (int)(loc.getLongitude() * 1E6));
			updateMapOverlays(mapView);
			if(UFCMApplication.followUserLocation) {
				mapView.getController().animateTo(curLocation);
				mapView.getController().setZoom(DEFAULT_ZOOM_LEVEL);
			}
		}
	}

	private void newBldgLocation(GeoPoint geo) {
		bldgLocation = geo;
		updateMapOverlays(mapView);
		if(UFCMApplication.followUserLocation) {
			mapView.getController().animateTo(bldgLocation);
			mapView.getController().setZoom(PINNED_ZOOM_LEVEL);
			UFCMApplication.followUserLocation = false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.menuMyLocation:
			if(curLocation != null) {
				mapView.getController().animateTo(curLocation);
				mapView.getController().setZoom(DEFAULT_ZOOM_LEVEL);
				UFCMApplication.followUserLocation = true;
			}
			return true;
		case R.id.menuSearch:
			onSearchRequested();
			return true;
		case R.id.menuViewMode:
			mapView.setSatellite(!mapView.isSatellite());
			return true;
		case R.id.menuAbout:
			aboutApp();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void aboutApp() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.copyright) + "\n\n" + getString(R.string.gpl_short_1) + "\n\n" + getString(R.string.gpl_short_2))
		.setCancelable(false)
		.setTitle(getString(R.string.app_name) + " " + getString(R.string.version_string))
		.setPositiveButton(getString(R.string.visit_website), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.project_url)));
				startActivity(browserIntent);
			}
		})
		.setNegativeButton(getString(R.string.close_dialog), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}