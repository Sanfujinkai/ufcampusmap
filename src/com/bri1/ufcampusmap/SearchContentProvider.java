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

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class SearchContentProvider extends ContentProvider {

	String TAG = "SearchContentProvider";

	public static String AUTHORITY = "com.bri1.ufcampusmap.SearchContentProvider";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/search");

	public static final String NAME_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.bri1.ufcampusmap.searchable";
	public static final String ADDR_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.bri1.ufcampusmap.searchable";

	private BuildingDatabase db;

	private static final int SEARCH_BLDGS = 0;
	private static final int GET_BLDG = 1;
	private static final int SEARCH_SUGGEST = 2;
	private static final int REFRESH_SHORTCUT = 3;
	private static final UriMatcher sURIMatcher = buildUriMatcher();

	private static UriMatcher buildUriMatcher() {
		UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		matcher.addURI(AUTHORITY, "search/", SEARCH_BLDGS);
		matcher.addURI(AUTHORITY, "search/#", GET_BLDG);
		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);
		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT, REFRESH_SHORTCUT);
		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*", REFRESH_SHORTCUT);
		return matcher;
	}

	@Override
	public boolean onCreate() {
		db = new BuildingDatabase(getContext()); 
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		switch(sURIMatcher.match(uri)) {
		case SEARCH_SUGGEST:
			if (selectionArgs == null) {
				throw new IllegalArgumentException(
						"selectionArgs must be provided for the Uri: " + uri);
			}
			return getSuggestions(selectionArgs[0]);
		case SEARCH_BLDGS:
			if (selectionArgs == null) {
				throw new IllegalArgumentException(
						"selectionArgs must be provided for the Uri: " + uri);
			}
			return search(selectionArgs[0]);
		case GET_BLDG:
			return getBuilding(uri);
		case REFRESH_SHORTCUT:
			return refreshShortcut(uri);
		default:
			throw new IllegalArgumentException("Unknown Uri: " + uri);
		}
	}

	private Cursor getSuggestions(String query) {
		query = query.toLowerCase();
		String[] columns = new String[] {
				BaseColumns._ID,
				BuildingDatabase.KEY_NAME,
				BuildingDatabase.KEY_ADDR,
				//SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, // to refresh shortcuts
				SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID};
		return db.getBuildingMatches(query, columns);
	}

	private Cursor search(String query) {
		query = query.toLowerCase();
		String[] columns = new String[] {
				BaseColumns._ID,
				BuildingDatabase.KEY_NAME,
				BuildingDatabase.KEY_ADDR};
		return db.getBuildingMatches(query, columns);
	}

	private Cursor getBuilding(Uri uri) {
		String rowId = uri.getLastPathSegment();
		String[] columns = new String[] {
				BuildingDatabase.KEY_NAME,
				BuildingDatabase.KEY_ADDR};
		return db.getBuilding(rowId, columns);
	}

	private Cursor refreshShortcut(Uri uri) {
		String rowId = uri.getLastPathSegment();
		String[] columns = new String[] {
				BaseColumns._ID,
				BuildingDatabase.KEY_NAME,
				BuildingDatabase.KEY_ADDR,
				SearchManager.SUGGEST_COLUMN_SHORTCUT_ID,
				SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID};
		return db.getBuilding(rowId, columns); 
	}

	@Override
	public String getType(Uri uri) {
		switch (sURIMatcher.match(uri)) {
		case SEARCH_BLDGS:
			return NAME_MIME_TYPE;
		case GET_BLDG:
			return ADDR_MIME_TYPE;
		case SEARCH_SUGGEST:
			return SearchManager.SUGGEST_MIME_TYPE;
		case REFRESH_SHORTCUT:
			return SearchManager.SHORTCUT_MIME_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

}
