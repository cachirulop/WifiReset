package com.cachirulop.wifireset;

import com.cachirulop.wifireset.R;
import com.cachirulop.wifireset.R.layout;
import com.cachirulop.wifireset.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

}
