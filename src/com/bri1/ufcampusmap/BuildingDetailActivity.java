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

import android.app.ActionBar;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bri1.ufcampusmap.contentprovider.BuildingContentProvider;

public class BuildingDetailActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);

		// Place "back button" in the action bar
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Get ID from extras
		final String id_str = getIntent().getStringExtra(BuildingContentProvider.CONTENT_ITEM_TYPE);
		final long id = Long.parseLong(id_str);
		
		// Grab info from database
		final String[] projection = {
				BuildingTable.COLUMN_BLDG,
				BuildingTable.COLUMN_CNAME,
				BuildingTable.COLUMN_ONAME,
				BuildingTable.COLUMN_ADDR,
				BuildingTable.COLUMN_ABBR,
				BuildingTable.COLUMN_DESC,
				BuildingTable.COLUMN_URL };
		Uri uri = Uri.withAppendedPath(BuildingContentProvider.CONTENT_URI, id_str);
		Cursor c = getContentResolver().query(uri, projection, null, null, null);
		if (!c.moveToFirst() || c.getCount() != 1)
			return;

		// Fill in TextViews from cursor
		((TextView) findViewById(R.id.buildingNumber)).setText(c.getString(0));
		((TextView) findViewById(R.id.cName)).setText(c.getString(1));
		if (!c.getString(1).equals(c.getString(2)))
			((TextView) findViewById(R.id.oName)).setText(c.getString(2));
		((TextView) findViewById(R.id.address)).setText(c.getString(3));
		((TextView) findViewById(R.id.abbrev)).setText(c.getString(4));
		((TextView) findViewById(R.id.description)).setText(c.getString(5));
		((TextView) findViewById(R.id.buildingUrl)).setText(c.getString(6));
		c.close();

		// Configure pin toggle button
		ToggleButton markedButton = (ToggleButton) findViewById(R.id.showingPin);
		boolean marked = UFCMApplication.selectedBuildings.contains(id);
		markedButton.setChecked(marked);
		markedButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
				if (isChecked) {
					UFCMApplication.selectedBuildings.add(id);
				} else {
					UFCMApplication.selectedBuildings.remove(id);
				}
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
