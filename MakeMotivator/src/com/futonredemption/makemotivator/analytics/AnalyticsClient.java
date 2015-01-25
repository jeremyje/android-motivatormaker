package com.futonredemption.makemotivator.analytics;

import java.util.concurrent.atomic.AtomicBoolean;

import org.beryl.diagnostics.ExceptionReporter;

import android.content.Context;

import com.futonredemption.makemotivator.Constants;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class AnalyticsClient {

	public static final int SCOPE_VISITOR = 1;
	public static final int SCOPE_SESSION = 2;
	public static final int SCOPE_PAGE = 3;
	
	public interface ICustomVariableCallback {
		String getName();
		String getValue(Context context);
		int getScopeLevel();
	}
	
	private final AtomicBoolean isStarted = new AtomicBoolean(false);
	private final Context context;
	private final GoogleAnalyticsTracker tracker;
	private final ICustomVariableCallback[] callbacks = new ICustomVariableCallback[5];
	
	public AnalyticsClient(final Context context) {
		this.context = context;
		tracker = GoogleAnalyticsTracker.getInstance();
		clearCallbacks();
	}
	
	protected void verifyStart() {
		if(!isStarted.get()) {
			start();
		}
	}
	public void start() {
		if(! isStarted.getAndSet(true)) {
			tracker.startNewSession(Constants.Application.AnalyticsId, this.context);
		}
	}
	
	public void stop() {
		if(isStarted.getAndSet(false)) {
			tracker.stopSession();
		}
	}
	
	public void submit() {
		verifyStart();
		tracker.dispatch();
	}
	
	public void clearCallbacks() {
		int len = callbacks.length;
		for(int i = 0; i < len; i++) {
			callbacks[i] = null;
		}
	}
	
	public void debugMode(boolean mode) {
		//tracker.setDryRun(mode);
		tracker.setDebug(mode);
	}
	
	public void setCustomVariableCallback(int variableId, ICustomVariableCallback callback) {
		if(variableId >= 1 && variableId <= 5) {
			callbacks[variableId - 1] = callback;
		} else {
			throw new IndexOutOfBoundsException("VariableId must be between 1 and 5.");
		}
	}
	
	protected void bindCustomVariables() {
		ICustomVariableCallback callback;
		int len = callbacks.length;
		for(int i = 0; i < len; i++) {
			callback = callbacks[i];
			if(callback != null) {
				tracker.setCustomVar(i + 1, callback.getName(),
						callback.getValue(context), callback.getScopeLevel());
			}
		}
	}
	
	public void pageView() {
		trackPageView(context.getClass().getName());
	}
	
	public void eventFail(String category, String action, String label) {
		trackEvent(category, action, label, 0);
	}
	
	public void eventSuccess(String category, String action, String label) {
		trackEvent(category, action, label, 1);
	}

	private void trackPageView(final String pageName) {
		verifyStart();
		try {
			tracker.trackPageView("/" + pageName);
		} catch(Exception e) {
			ExceptionReporter.report(e);
		}
	}
	private void trackEvent(final String category, final String action, final String label, final int value) {
		verifyStart();
		try {
			tracker.trackEvent(category, action, label, value);
		} catch(Exception e) {
			ExceptionReporter.report(e);
		}
	}
}
