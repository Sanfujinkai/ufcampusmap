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

import java.io.IOException;

import org.json.JSONException;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BuildingDatabaseHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "ufcampusmap.db";
	private static final int DATABASE_VERSION = 1;
	private final Resources resources;
	
	BuildingDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		resources = context.getResources();
	}
	
	// Method is called during creation of the database
	@Override
	public void onCreate(SQLiteDatabase database) {
		try {
			BuildingTable.reCreate(database, resources);
		} catch(IOException e) {
			throw new RuntimeException(e);
		} catch(JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	// Method is called during an upgrade of the database, e.g. if you increase the database version
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		android.util.Log.w(UFCMApplication.LOG_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		try {
			BuildingTable.reCreate(database, resources);
		} catch(IOException e) {
			throw new RuntimeException(e);
		} catch(JSONException e) {
			throw new RuntimeException(e);
		}
	}

}
