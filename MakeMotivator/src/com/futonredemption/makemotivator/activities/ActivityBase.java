package com.futonredemption.makemotivator.activities;

import org.beryl.app.AndroidVersion;
import org.beryl.app.ContractRegistry;
import org.beryl.app.IContractMediator;

import com.futonredemption.makemotivator.Constants;
import com.futonredemption.makemotivator.R;
import com.futonredemption.makemotivator.analytics.AnalyticsClient;
import com.futonredemption.makemotivator.analytics.AnalyticsClient.ICustomVariableCallback;
import com.futonredemption.makemotivator.analytics.CustomVariablesFactory;
import com.futonredemption.makemotivator.contracts.IAnalyticsCommand;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;

public abstract class ActivityBase extends FragmentActivity implements IContractMediator {
	final private ContractRegistry contracts = new ContractRegistry();
	private AnalyticsClient analytics = null;
	public ContractRegistry getContractRegistry() {
		return contracts;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupWindow();
		contracts.add(this);
		
		if(analytics == null) {
			analytics = new AnalyticsClient(this);
			setupAnalytics();
			analytics.start();
			if(savedInstanceState == null) {
				analytics.pageView();
			}
		}
	}
	
	private void setupAnalytics() {
		CustomVariablesFactory.setupDefaultVariables(analytics);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		contracts.remove(this);
		
		final SharedPreferences prefs = getPreferences();
		boolean isAllowed = prefs.getBoolean(Constants.Preferences.Key_Analytics, Constants.Preferences.DefaultValue_Analytics);
		
		if(isAllowed) {
			analytics.submit();
		}
		
		analytics.stop();
	}
	
	protected SharedPreferences getPreferences() {
		return this.getSharedPreferences(Constants.Preferences.ApplicationKey, MODE_PRIVATE);
	}
	
	protected void setupWindow() {
		// Set 32-bit color mode for pre-honeycomb versions.
		if(AndroidVersion.isBeforeHoneycomb()) {
			final Window window = getWindow();
			window.setFormat(PixelFormat.RGBA_8888);
		}
	}
	
	@TargetApi(11)
	protected void showUpAffordanceOnActionBar() {
		if(AndroidVersion.isHoneycombOrHigher()) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}
	
	protected void goHome() {
		Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
	}
	
	protected boolean isPhoneFormFactor() {
		return ! getResources().getBoolean(R.bool.IsTablet);
	}
	
	protected boolean isTabletFormFactor() {
		return getResources().getBoolean(R.bool.IsTablet);
	}
	
	protected boolean isLandscapeOrientation() {
		return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
	}
	
	protected void hideNotificationBar() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	
	protected AnalyticsClient getAnalytics() {
		return this.analytics;
	}
	
	public void trackerEventFail(String category, String action, String label) {
		analytics.eventFail(category, action, label);
	}

	public void trackerEventSuccess(String category, String action, String label) {
		analytics.eventSuccess(category, action, label);
	}
	
	IAnalyticsCommand contractIAnalyticsCommand = new IAnalyticsCommand() {

		public void eventFail(String category, String action, String label) {
			analytics.eventFail(category, action, label);
		}

		public void eventSuccess(String category, String action, String label) {
			analytics.eventSuccess(category, action, label);
		}

		public void registerCustomVariable(int variableId,
				ICustomVariableCallback callback) {
			analytics.setCustomVariableCallback(variableId, callback);
		}
	};
}
