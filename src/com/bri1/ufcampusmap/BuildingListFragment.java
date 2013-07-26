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

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.bri1.ufcampusmap.contentprovider.BuildingContentProvider;

public class BuildingListFragment extends ListFragment
implements OnQueryTextListener, OnCloseListener,
LoaderManager.LoaderCallbacks<Cursor> {

	// This is the Adapter being used to display the list's data.
	SimpleCursorAdapter mAdapter;

	// The SearchView for doing filtering.
	SearchView mSearchView;

	// If non-null, this is the current filter the user has provided.
	String mCurFilter;

	@Override public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Give some text to display if there is no data.
		setEmptyText(getResources().getString(R.string.no_results));

		// We have a menu item to show in action bar.
		setHasOptionsMenu(true);

		// Create an empty adapter we will use to display the loaded data.
		mAdapter = new SimpleCursorAdapter(getActivity(),
				android.R.layout.simple_list_item_2, null,
				new String[] { BuildingTable.COLUMN_CNAME, BuildingTable.COLUMN_ADDR },
				new int[] { android.R.id.text1, android.R.id.text2 }, 0);
		setListAdapter(mAdapter);

		// Start out with a progress indicator.
		setListShown(false);

		// Prepare the loader.  Either re-connect with an existing one,
		// or start a new one.
		getLoaderManager().initLoader(0, null, this);
		
		// Handle long-presses
		getListView().setLongClickable(true);
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
				Intent i = new Intent(getActivity(), BuildingDetailActivity.class);
				i.putExtra(BuildingContentProvider.CONTENT_ITEM_TYPE, id);
				startActivity(i);
				return true;
			}
		});
	}

	public static class MySearchView extends SearchView {
		public MySearchView(Context context) {
			super(context);
		}

		// The normal SearchView doesn't clear its search text when
		// collapsed, so we will do this for it.
		@Override
		public void onActionViewCollapsed() {
			setQuery("", false);
			super.onActionViewCollapsed();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Place an action bar item for searching.
		MenuItem item = menu.add("Search");
		item.setIcon(android.R.drawable.ic_menu_search);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM
				| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		mSearchView = new MySearchView(getActivity());
		mSearchView.setOnQueryTextListener(this);
		mSearchView.setOnCloseListener(this);
		mSearchView.setIconifiedByDefault(true);
		item.setActionView(mSearchView);
		// Hack to fix black search query text on dark background
		int id = mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
		TextView textView = (TextView) mSearchView.findViewById(id);
		textView.setTextColor(Color.WHITE);
	}

	public boolean onQueryTextChange(String newText) {
		// Called when the action bar search text has changed.  Update
		// the search filter, and restart the loader to do a new query
		// with this filter.
		if (!isVisible())
			return false;
		String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
		// Don't do anything if the filter hasn't actually changed.
		// Prevents restarting the loader when restoring state.
		if (mCurFilter == null && newFilter == null) {
			return true;
		}
		if (mCurFilter != null && mCurFilter.equals(newFilter)) {
			return true;
		}
		mCurFilter = newFilter;
		getLoaderManager().restartLoader(0, null, this);
		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		//Log.i(UFCMApplication.LOG_TAG, "onQueryTextSubmit: " + query);
		mSearchView.clearFocus();
		return true;
	}

	@Override
	public boolean onClose() {
		if (!TextUtils.isEmpty(mSearchView.getQuery())) {
			mSearchView.setQuery(null, true);
		}
		return true;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if (UFCMApplication.selectedBuildings.add(id)) {
			Toast.makeText(getActivity(), getResources().getString(R.string.toast_added), Toast.LENGTH_SHORT).show();
		} else {
			UFCMApplication.selectedBuildings.remove(id);
			Toast.makeText(getActivity(), getResources().getString(R.string.toast_removed), Toast.LENGTH_SHORT).show();
		}
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// This is called when a new Loader needs to be created.  This
		// sample only has one Loader, so we don't care about the ID.
		final String[] columns = new String[] { BuildingTable.COLUMN_ID,
				BuildingTable.COLUMN_CNAME, BuildingTable.COLUMN_ADDR };
		
		String sortOrder = null;
		String selection = null;
		String[] selectionArgs = null;
		
		if (mCurFilter != null) {
			selection = BuildingTable.COLUMN_CNAME + " LIKE ? OR " +
						BuildingTable.COLUMN_ONAME + " LIKE ? OR " +
						BuildingTable.COLUMN_ABBR  + " LIKE ? OR " +
						BuildingTable.COLUMN_ADDR  + " LIKE ?";
			selectionArgs = new String[] {"%" + mCurFilter + "%", "%" + mCurFilter + "%", "%" + mCurFilter + "%", "%" + mCurFilter + "%"};
		} else {
			sortOrder = BuildingTable.COLUMN_CNAME + " ASC";
		}

		CursorLoader cursorLoader = new CursorLoader(getActivity(),
				BuildingContentProvider.CONTENT_URI, columns,
				selection, selectionArgs, sortOrder);
		
		return cursorLoader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Swap the new cursor in.  (The framework will take care of closing the
		// old cursor once we return.)
		mAdapter.swapCursor(data);

		// The list should now be shown.
		if (isResumed()) {
			setListShown(true);
		} else {
			setListShownNoAnimation(true);
		}
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		// This is called when the last Cursor provided to onLoadFinished()
		// above is about to be closed.  We need to make sure we are no
		// longer using it.
		mAdapter.swapCursor(null);
	}
	
}