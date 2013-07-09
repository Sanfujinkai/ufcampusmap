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

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class BuildingListFragment extends ListFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_buildinglist, container, false);

		// Add "clear" button to the search field
		final EditText searchField = (EditText) rootView.findViewById(R.id.searchField); 
		final Drawable x = getResources().getDrawable(android.R.drawable.presence_offline);
		x.setBounds(0, 0, x.getIntrinsicWidth(), x.getIntrinsicHeight());
		//searchField.setCompoundDrawables(null, null, null, null);
		searchField.setOnTouchListener(new OnTouchListener() {
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		        if (searchField.getCompoundDrawables()[2] == null) {
		            return false;
		        }
		        if (event.getAction() != MotionEvent.ACTION_UP) {
		            return false;
		        }
		        if (event.getX() > searchField.getWidth() - searchField.getPaddingRight() - x.getIntrinsicWidth()) {
		        	searchField.setText("");
		        	searchField.setCompoundDrawables(null, null, null, null);
		        }
		        return false;
		    }
		});
		searchField.addTextChangedListener(new TextWatcher() {
		    @Override
		    public void onTextChanged(CharSequence s, int start, int before, int count) {
		    	searchField.setCompoundDrawables(null, null, searchField.getText().toString().equals("") ? null : x, null);
		    }
		    @Override
		    public void afterTextChanged(Editable arg0) {
		    }
		    @Override
		    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		    }
		});
		
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// TODO
		String[] values = new String[] { "test1", "test2" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		TextView tv = (TextView) this.getView().findViewById(R.id.numResults);
		tv.setText("position=" + position + "; id=" + id);
		// TODO
	}

}
