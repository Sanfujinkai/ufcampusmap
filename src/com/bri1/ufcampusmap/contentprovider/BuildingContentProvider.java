package com.bri1.ufcampusmap.contentprovider;

import com.bri1.ufcampusmap.BuildingDatabaseHelper;
import com.bri1.ufcampusmap.BuildingTable;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class BuildingContentProvider extends ContentProvider {

	private BuildingDatabaseHelper database;

	private static final int BUILDINGS = 10;
	private static final int BUILDING_ID = 20;

	private static final String AUTHORITY = "com.bri1.ufcampusmap.contentprovider";

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
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(BuildingTable.TABLE_NAME);
		int uriType = sURIMatcher.match(uri);

		switch (uriType) {
		case BUILDINGS:
			break;
		case BUILDING_ID:
			queryBuilder.appendWhere(BuildingTable.COLUMN_ID + "=" + uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI for query");
		}

		SQLiteDatabase db = database.getReadableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		throw new IllegalArgumentException("Unknown URI for insert");
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new IllegalArgumentException("Unknown URI for delete");
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		throw new IllegalArgumentException("Unknown URI for update");
	}

}