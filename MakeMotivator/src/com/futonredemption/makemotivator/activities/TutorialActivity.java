package com.futonredemption.makemotivator.activities;

import android.os.Bundle;
import android.view.MenuItem;

import com.futonredemption.makemotivator.R;
import com.futonredemption.makemotivator.contracts.IGoHomeCommand;

public class TutorialActivity extends ActivityBase {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_tutorial);
		showUpAffordanceOnActionBar();
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
	
	final IGoHomeCommand contractGoHomeCommand = new IGoHomeCommand() {
		public void goHome() {
			TutorialActivity.this.goHome();
		}
	};
}
