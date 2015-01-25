package com.futonredemption.makemotivator.activities;

import org.beryl.app.AndroidVersion;

import com.futonredemption.makemotivator.R;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

public class MainPreferencesActivity extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs_main);
		showUpAffordanceOnActionBar();
	}
	
	@TargetApi(11)
	protected void showUpAffordanceOnActionBar() {
		if(AndroidVersion.isHoneycombOrHigher()) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home: {
			goHome();
		} break;
		
		default:
			return super.onOptionsItemSelected(item);
		}

		return true;
	}
	
	protected void goHome() {
		Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
	}
}
