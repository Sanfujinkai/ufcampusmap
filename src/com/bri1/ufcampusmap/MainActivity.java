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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.ArrayAdapter;

public class MainActivity extends FragmentActivity implements ActionBar.OnNavigationListener {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
			// Specify a SpinnerAdapter to populate the dropdown list.
			new ArrayAdapter<String>(
				actionBar.getThemedContext(),
				android.R.layout.simple_list_item_1,
				android.R.id.text1,
				new String[] {
					getString(R.string.title_buildings),
					getString(R.string.title_map)
				}),
		this);
	}


	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
				savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
				getActionBar().getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		// Associate appropriate intents with menu items
		menu.findItem(R.id.action_periods).setIntent(new Intent(this, PeriodsActivity.class));
		menu.findItem(R.id.action_isis).setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.isis.ufl.edu/mobile/")));
		menu.findItem(R.id.action_transloc).setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://ufl.transloc.com/m/")));
		menu.findItem(R.id.action_about).setIntent(new Intent(this, AboutActivity.class));
		return true;
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		Fragment fragment;
		if (position == 0) {
			fragment = new BuildingListFragment();
		} else {
			fragment = new CampusMapFragment();
		}
		getSupportFragmentManager().beginTransaction()
			.replace(R.id.container, fragment)
			.commit();
		return true;
	}

}
