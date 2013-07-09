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

import com.google.android.gms.common.GooglePlayServicesUtil;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class AboutFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_about, container, false);
		
		// Attach event handler to button 
		Button gapiInfoButton = (Button) rootView.findViewById(R.id.gapi_info_button);
		gapiInfoButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Context ctx = view.getContext();
				String LicenseInfo = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(ctx);
		        AlertDialog.Builder LicenseDialog = new AlertDialog.Builder(ctx);
				LicenseDialog.setMessage(LicenseInfo);
				LicenseDialog.show();
		    }
		});
		
		return rootView;
	}
	
}
