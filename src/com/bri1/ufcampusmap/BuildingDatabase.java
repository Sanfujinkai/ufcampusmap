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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;

public class BuildingDatabase {
	
	private static final String TAG = "BuildingDatabase";
	
	public static final String KEY_NAME = SearchManager.SUGGEST_COLUMN_TEXT_1;
	public static final String KEY_ADDR = SearchManager.SUGGEST_COLUMN_TEXT_2;
	
	private static final String DATABASE_NAME = "campus";
	private static final String FTS_VIRTUAL_TABLE = "FTScampus";
	private static final int DATABASE_VERSION = 2;
	
	private final DatabaseOpenHelper dbOpenHelper;
	private static final HashMap<String, String> columnMap = buildColumnMap();
	
	public BuildingDatabase(Context context) {
		dbOpenHelper = new DatabaseOpenHelper(context);
	}
	
	private static HashMap<String, String> buildColumnMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(KEY_NAME, KEY_NAME);
		map.put(KEY_ADDR, KEY_ADDR);
		map.put(BaseColumns._ID, "rowid AS " + BaseColumns._ID);
		map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "rowid AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
		map.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, "rowid AS" + SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);
		return map;
	}
	
	public Cursor getBuilding(String rowId, String[] columns) {
		String selection = "rowid = ?";
		String[] selectionArgs = new String[] {rowId};
		return query(selection, selectionArgs, columns); // SELECT <columns> FROM <table> WHERE rowid = <rowId>
	}
	
	public Cursor getBuildingMatches(String query, String[] columns) {
		String selection = KEY_NAME + " MATCH ?"; // change KEY_NAME to FTS_VIRTUAL_TABLE to search the whole table
		String[] selectionArgs = new String[] {query + "*"};
		return query(selection, selectionArgs, columns); // SELECT <columns> FROM <table> WHERE <KEY_WORD> MATCH 'query*'
	}
	
	private Cursor query(String selection, String[] selectionArgs, String[] columns) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(FTS_VIRTUAL_TABLE);
		builder.setProjectionMap(columnMap);
		Cursor cursor = builder.query(dbOpenHelper.getReadableDatabase(), columns, selection, selectionArgs, null, null, null);
		if(cursor != null && !cursor.moveToFirst()) {
			cursor.close();
			return null;
		}
		return cursor;
	}
	
	private static class DatabaseOpenHelper extends SQLiteOpenHelper {
	
		private final Context helperContext;
		private SQLiteDatabase db;
		
		private static final String FTS_TABLE_CREATE = "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE + " USING fts3(" + KEY_NAME + ", " + KEY_ADDR + ");";
		
		DatabaseOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			helperContext = context;
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(FTS_TABLE_CREATE);
			this.db = db;
			new Thread(new Runnable() {
				public void run() {
					try {
						loadSearchJson();
					} catch(IOException e) {
						throw new RuntimeException(e);
					} catch(JSONException e) {
						throw new RuntimeException(e);
					}
				}
			}).start();
		}
	
		public void loadSearchJson() throws IOException, JSONException {
			android.util.Log.w(UFCMApplication.LOG_TAG, "Loading JSON data...hold on for just a bit!");
			final Resources resources = helperContext.getResources();
			InputStream inputStream = resources.openRawResource(R.raw.search);
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder sb = new StringBuilder();
			try {
				String line;
				while((line = br.readLine()) != null)
					sb.append(line);
			} finally {
				br.close();
			}
			JSONArray jsonArray = new JSONArray(sb.toString());
			int i;
			for(i = 0; i < jsonArray.length(); i++) {
				JSONObject obj = jsonArray.getJSONObject(i);
				android.util.Log.w(UFCMApplication.LOG_TAG, "Processing JSON object with ID: " + obj.getString("bldgID"));
				addBuilding(
					obj.getDouble("lat"),
					obj.getDouble("lng"),
					obj.getString("bldg"), // note: the JSON property bldg is usually an int, but sometimes there can be characters in it, so a string must be used
					obj.getString("cname"),
					obj.getString("oname"),
					obj.getString("add1"),
					obj.getString("abbrev"),
					obj.getString("descrip"),
					obj.getString("url")
				);
			}
			android.util.Log.w(UFCMApplication.LOG_TAG, "Inserted " + i + " JSON objects into the database");
		}
		
		public long addBuilding(double lat, double lng, String bldg, String cname, String oname, String addr, String abbrev, String desc, String url) {
			ContentValues values = new ContentValues();
			values.put(KEY_NAME, cname);
			values.put(KEY_ADDR, addr);
			// TODO put the rest of the values
			return db.insert(FTS_VIRTUAL_TABLE, null, values);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			android.util.Log.w(UFCMApplication.LOG_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
			onCreate(db);
		}
	
	} // end DatabaseOpenHelper class
}
