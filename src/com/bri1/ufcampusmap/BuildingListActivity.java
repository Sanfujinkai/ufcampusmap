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

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.view.View.OnKeyListener;
import android.widget.AdapterView.OnItemClickListener;

public class BuildingListActivity extends ListActivity {

	private ListView listView;
	private TextView searchNoResults;
	private EditText searchField;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buildinglist);
		listView = getListView();
		searchNoResults = (TextView) findViewById(R.id.searchNoResults);
		searchField = (EditText) findViewById(R.id.searchField);

		// Setup key press handler for the text input field
		searchField.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
					return true;
				}
				return false;
			}
		});
		
		// Perform a query to (re-)populate the result list
		showResults(searchField.getText().toString());
	}

	private void showResults(String query) {
		Cursor cursor = managedQuery(SearchContentProvider.CONTENT_URI, null, null, new String[] {query}, null);

		if(cursor == null) {
			searchNoResults.setVisibility(View.VISIBLE);
		} else {
			searchNoResults.setVisibility(View.INVISIBLE);
			String[] from = new String[] { BuildingDatabase.KEY_NAME, BuildingDatabase.KEY_ADDR };
			int[] to = new int[] { R.id.campusBuildingName, R.id.campusBuildingDescription };
			SimpleCursorAdapter sca = new SimpleCursorAdapter(this, R.layout.result, cursor, from, to);
			listView.setAdapter(sca);
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// TODO
					finish();
				}
			});
		}
	}
	
}