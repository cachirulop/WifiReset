package com.cachirulop.wifireset.activity;

import android.app.Activity;
import android.os.Bundle;

import com.cachirulop.wifireset.R;

/**
 * Shows activity with the settings screen
 *  
 * @author david
 *
 */
public class SettingsActivity extends Activity {
	/**
	 * Load the settings screen. 
	 * The screen is based on the activity_settings layout.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
	}
}
