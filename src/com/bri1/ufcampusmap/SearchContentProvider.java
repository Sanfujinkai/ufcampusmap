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

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class SearchContentProvider extends ContentProvider {

	private BuildingDatabaseHelper database;

	// Used for the UriMatcher
	private static final int BUILDINGS = 10;
	private static final int BUILDING_ID = 20;

	public static final String AUTHORITY = "com.bri1.ufcampusmap.SearchContentProvider";

	private static final String BASE_PATH = "buildings";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/buildings";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/building";

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, BUILDINGS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", BUILDING_ID);
	}

	@Override
	public boolean onCreate() {
		database = new BuildingDatabaseHelper(getContext()); 
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		// Using SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// Check if the caller has requested a column which does not exists
		checkColumns(projection);

		// Set the table
		queryBuilder.setTables(BuildingTable.TABLE_NAME);

		// Match the URI type
		int uriType = sURIMatcher.match(uri);
		switch(uriType) {
		case BUILDINGS:
			break;
		case BUILDING_ID:
			// Adding the ID to the original query
			queryBuilder.appendWhere(BuildingTable.COLUMN_ID + "=" + uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown Uri: " + uri);
		}

		SQLiteDatabase db = database.getReadableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	protected void checkColumns(String[] projection) {
		String[] available = {
				BuildingTable.COLUMN_ID,
				BuildingTable.COLUMN_LAT,
				BuildingTable.COLUMN_LNG,
				BuildingTable.COLUMN_BLDG,
				BuildingTable.COLUMN_CNAME,
				BuildingTable.COLUMN_ONAME,
				BuildingTable.COLUMN_ADDR,
				BuildingTable.COLUMN_ABBR,
				BuildingTable.COLUMN_DESC,
				BuildingTable.COLUMN_URL
		};
		if(projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
			// Check if all columns which are requested are available
			if(!availableColumns.containsAll(requestedColumns))
				throw new IllegalArgumentException("Unknown columns in projection");
		}
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

}
