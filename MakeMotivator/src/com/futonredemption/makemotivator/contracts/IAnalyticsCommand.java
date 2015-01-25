package com.futonredemption.makemotivator.contracts;

import org.beryl.app.RegisterableContract;

import com.futonredemption.makemotivator.analytics.AnalyticsClient.ICustomVariableCallback;

public interface IAnalyticsCommand extends RegisterableContract {
	void eventFail(String category, String action, String label);
	void eventSuccess(String category, String action, String label);
	void registerCustomVariable(int variableId, ICustomVariableCallback callback);
}
