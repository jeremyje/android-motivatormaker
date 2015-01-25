package com.futonredemption.makemotivator.contracts;

import org.beryl.app.RegisterableContract;

public interface IActivityController extends RegisterableContract {
	void goBack();
	void hideVirtualKeyboard();
}
