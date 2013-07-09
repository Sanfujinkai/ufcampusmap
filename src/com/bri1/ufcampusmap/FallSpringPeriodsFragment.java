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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

public class FallSpringPeriodsFragment extends Fragment {
	
	private static final String[] periods = new String[] {
		"Period", "Start", "End",
		"1", "7:25", "8:15 am",
		"2", "8:30", "9:20 am",
		"3", "9:35", "10:25 am",
		"4", "10:40", "11:30 am",
		"5", "11:45", "12:35 pm",
		"6", "12:50", "1:40 pm",
		"7", "1:55", "2:45 pm",
		"8", "3:00", "3:50 pm",
		"9", "4:05", "4:55 pm",
		"10", "5:10", "6:00 pm",
		"11", "6:15", "7:05 pm",
		"E1", "7:20", "8:10 pm",
		"E2", "8:20", "9:10 pm",
		"E3", "9:20", "10:10 pm"
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_fallspringperiods, container, false);

		// Populate gridview with data
		GridView periodTimesGrid = (GridView) rootView.findViewById(R.id.fallspringTimesGrid);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_1, periods);
		periodTimesGrid.setAdapter(adapter);

		return rootView;
	}
	
}
