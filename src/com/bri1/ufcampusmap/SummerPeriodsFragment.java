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

public class SummerPeriodsFragment extends Fragment {
	
	private static final String[] periods = new String[] {
		"Period", "Start", "End",
		"1", "8:00", "9:15 am",
		"2", "9:30", "10:45 am",
		"3", "11:00", "12:15 pm",
		"4", "12:30", "1:45 pm",
		"5", "2:00", "3:15 pm",
		"6", "3:30 ", "4:45 pm",
		"7", "5:00", "6:15 pm",
		"E1", "7:00", "8:15 pm",
		"E2", "8:30", "9:45 pm"
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_summerperiods, container, false);
		
		// Populate gridview with data
		GridView periodTimesGrid = (GridView) rootView.findViewById(R.id.summerTimesGrid);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_1, periods);
		periodTimesGrid.setAdapter(adapter);
		
		return rootView;
	}
	
}
