package com.android.debug;

import org.beryl.app.diagnostics.reporting.CrashReporter;
import org.beryl.diagnostics.ExceptionReporter;
import org.beryl.diagnostics.StrictModeEnabler;

import android.content.Intent;
import android.view.View;

import com.android.debug.hv.ViewServer;
import com.futonredemption.makemotivator.activities.ActivityBase;

public abstract class DebuggableFragmentActivity extends ActivityBase {

	private boolean inDebugMode = false;
	private boolean usingViewServer = false;
	private int mCounter;

	public void enableBugReporting() {
		CrashReporter.initializeReporter(getApplication(), "futonredemption@gmail.com");
		ExceptionReporter.setCustomHandler(CrashReporter.createReporter());
	}
	
	public boolean isInDebugMode() {
		return inDebugMode;
	}
	
	public void startDebugMode() {
		
		inDebugMode = true;
		ExceptionReporter.enable();
		enableViewServer();
		getAnalytics().debugMode(true);

        StrictModeEnabler.enableOnThread();
	}

	private void enableViewServer() {
		final Intent intent = getIntent();

		if(intent != null) {
			if (intent.getExtras() != null)
				mCounter = getIntent().getExtras().getInt("counter");
		}

        ViewServer.get(this).addWindow(this);
        usingViewServer = true;
	}

	public void nextActivity(View v) {
    	Intent intent = new Intent(this, getClass());
    	intent.putExtra("counter", mCounter + 1);
		startActivity(intent);
    }

	@Override
	protected void onDestroy() {
    	super.onDestroy();

    	if(usingViewServer) {
    		ViewServer.get(this).removeWindow(this);
    	}
    }

    @Override
    public void onResume() {
    	super.onResume();

    	if(usingViewServer) {
    		ViewServer.get(this).setFocusedWindow(this);
    	}
    }
}
