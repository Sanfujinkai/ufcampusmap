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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;

public class BuildingTable {

	public static final String TABLE_NAME = "buildings";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_LAT = "lat";
	public static final String COLUMN_LNG = "lng";
	public static final String COLUMN_BLDG = "bldg";
	public static final String COLUMN_CNAME = "cname";
	public static final String COLUMN_ONAME = "oname";
	public static final String COLUMN_ADDR = "add1";
	public static final String COLUMN_ABBR = "abbrev";
	public static final String COLUMN_DESC = "descrip";
	public static final String COLUMN_URL = "url";

	private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME + "(" +
			COLUMN_ID    + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			COLUMN_LAT   + " REAL NOT NULL, " +
			COLUMN_LNG   + " REAL NOT NULL, " +
			COLUMN_BLDG  + " TEXT NOT NULL, " +
			COLUMN_CNAME + " TEXT NOT NULL, " +
			COLUMN_ONAME + " TEXT, " +
			COLUMN_ADDR  + " TEXT, " +
			COLUMN_ABBR  + " TEXT, " +
			COLUMN_DESC  + " TEXT, " +
			COLUMN_URL   + " TEXT" +
			");";

	public static void reCreate(SQLiteDatabase database, Resources resources) throws IOException, JSONException {
		// Apply the table's schema to the database 
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		android.util.Log.i(UFCMApplication.LOG_TAG, "Applying schema for table " + TABLE_NAME);
		database.execSQL(DATABASE_CREATE);

		// Read JSON resource into memory
		android.util.Log.i(UFCMApplication.LOG_TAG, "Importing JSON data... hold on for just a bit!");
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

		// Process/import JSON data into table
		JSONArray jsonArray = new JSONArray(sb.toString());
		int i;
		for(i = 0; i < jsonArray.length(); i++) {
			JSONObject obj = jsonArray.getJSONObject(i);
			insertBuilding(database,
					obj.getDouble(COLUMN_LAT),
					obj.getDouble(COLUMN_LNG),
					obj.getString(COLUMN_BLDG), // note: the JSON property bldg is usually an integer, but sometimes there can be characters in it, so a string must be used
					obj.getString(COLUMN_CNAME),
					obj.getString(COLUMN_ONAME),
					obj.getString(COLUMN_ADDR),
					obj.getString(COLUMN_ABBR),
					obj.getString(COLUMN_DESC),
					obj.getString(COLUMN_URL)
					);
		}
		android.util.Log.i(UFCMApplication.LOG_TAG, "Inserted " + i + " JSON objects into the database");
	}

	private static void insertBuilding(SQLiteDatabase database, double lat, double lng, String bldg, String cname, String oname, String addr, String abbr, String desc, String url) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_LAT, lat);
		cv.put(COLUMN_LNG, lng);
		cv.put(COLUMN_BLDG, bldg);
		cv.put(COLUMN_CNAME, cname);
		cv.put(COLUMN_ONAME, oname);
		cv.put(COLUMN_ADDR, addr);
		cv.put(COLUMN_ABBR, abbr);
		cv.put(COLUMN_DESC, desc);
		cv.put(COLUMN_URL, url);
		database.insert(TABLE_NAME, null, cv);
	}
	
}
