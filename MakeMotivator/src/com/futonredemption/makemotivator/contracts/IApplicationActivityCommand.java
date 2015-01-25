package com.futonredemption.makemotivator.contracts;

import org.beryl.app.RegisterableContract;

public interface IApplicationActivityCommand extends RegisterableContract {
	void showAboutApplication();
	void toggleCustomizer();
	void showSettings();
	void showWebGallery();
	void showTutorial();
	void showOverflowMenu();
	void showNewUserIntro();
}
