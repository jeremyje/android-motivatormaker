package com.futonredemption.makemotivator.analytics;

import com.futonredemption.makemotivator.R;
import com.futonredemption.makemotivator.analytics.AnalyticsClient.ICustomVariableCallback;
import com.futonredemption.makemotivator.contracts.IAnalyticsCommand;
import com.futonredemption.makemotivator.poster.IPosterComposition;

import android.content.Context;
import android.content.res.Configuration;

public class CustomVariablesFactory {

	public static void setupDefaultVariables(AnalyticsClient client) {
		client.setCustomVariableCallback(1, new ICustomVariableCallback() {
			public String getValue(Context context) {
				if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					return "Landscape";
				} else {
					return "Portrait";
				}
			}
			public String getName() { return "Orientation"; }
			public int getScopeLevel() { return AnalyticsClient.SCOPE_PAGE; }
		});
		
		client.setCustomVariableCallback(5, new ICustomVariableCallback() {
			public String getValue(Context context) {
				if(context.getResources().getBoolean(R.bool.IsTablet)) {
					return "Tablet";
				} else {
					return "Phone";
				}
			}
			public String getName() { return "FormFactor"; }
			public int getScopeLevel() { return AnalyticsClient.SCOPE_VISITOR; }
		});
	}
	public static void setupPosterComposition(final IAnalyticsCommand cmd, final IPosterComposition composition) {

		cmd.registerCustomVariable(2, new ICustomVariableCallback() {
			public String getValue(Context context) { return composition.getFileFormat(); }
			public String getName() { return "FileFormat"; }
			public int getScopeLevel() { return AnalyticsClient.SCOPE_PAGE; }
		});
		
		cmd.registerCustomVariable(3, new ICustomVariableCallback() {
			public String getValue(Context context) { return Float.toString(composition.getSizeMultiplier()); }
			public String getName() { return "SizeMultiplier"; }
			public int getScopeLevel() { return AnalyticsClient.SCOPE_PAGE; }
		});
		
		cmd.registerCustomVariable(4, new ICustomVariableCallback() {
			public String getValue(Context context) { return composition.getThemeTag(); }
			public String getName() { return "ThemeTag"; }
			public int getScopeLevel() { return AnalyticsClient.SCOPE_PAGE; }
		});
	}
}
