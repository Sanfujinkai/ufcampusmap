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
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.view.View.OnKeyListener;

public class BuildingListActivity extends ListActivity {

	private TextView numResults;
	private EditText searchField;
	private SimpleCursorAdapter adapter;
	private BuildingDatabaseHelper dbHelper;
	private Cursor cursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buildinglist);
		
		numResults = (TextView) findViewById(R.id.numResults);
		searchField = (EditText) findViewById(R.id.searchField);
		
		// Setup key press handler for the text input field
		searchField.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_UP) {
					showResults();
					if(keyCode == KeyEvent.KEYCODE_ENTER) {
						InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
					}
				}
				return false;
			}
		});
		
		// Initialize the database helper
		dbHelper = new BuildingDatabaseHelper(this);
		
		// Perform a query to (re-)populate the results list
		showResults();
	}

	private void showResults() {
		// Fields from the database (projection) must include the _id column for the adapter to work
		String[] columns_id = new String[] { BuildingTable.COLUMN_ID, BuildingTable.COLUMN_CNAME, BuildingTable.COLUMN_ONAME, BuildingTable.COLUMN_ADDR };
		String[] columns = new String[] { BuildingTable.COLUMN_CNAME, BuildingTable.COLUMN_ADDR };
		
		// Fields on the UI to which we map
		int[] destination = new int[] { R.id.campusBuildingName, R.id.campusBuildingDescription };
		
		// Open the database
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		// Prepare the search query
		String query = searchField.getText().toString();
		String where_clause = null;
		String[] where_args = null;
		if(query.length() > 0) {
			where_clause = BuildingTable.COLUMN_CNAME + " LIKE ? OR " + BuildingTable.COLUMN_ONAME + " LIKE ?";
			where_args = new String[] {"%" + query + "%", "%" + query + "%"};
		}
		
		// Query the database
		cursor = db.query(BuildingTable.TABLE_NAME, columns_id, where_clause, where_args, null, null, BuildingTable.COLUMN_CNAME);
		startManagingCursor(cursor);
		
		// Hide or show the "no results" message
		if(cursor.getCount() == 1) {
			numResults.setText("Listing 1 building.");
		} else {
			numResults.setText("Listing " + cursor.getCount() + " buildings.");
		}
		
		// Bridge the query's results to the ListView
		adapter = new SimpleCursorAdapter(this, R.layout.result, cursor, columns, destination);
		setListAdapter(adapter);
		
		// Close the database
		db.close();
	}
	
	// Returns to the original activity once an entry is clicked
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		cursor.moveToPosition(position);
		UFCMApplication.dbCurrentId = cursor.getInt(0);
		finish();
	}
	
}