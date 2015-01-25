package com.futonredemption.makemotivator.contracts;

import org.beryl.app.RegisterableContract;

public interface ICustomizableCommand extends RegisterableContract {
	int getForecolor();
	/* void setForecolor(int color); */
	void blockLocalEdits();
	void unblockLocalEdits();
}
