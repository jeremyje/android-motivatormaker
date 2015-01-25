package com.futonredemption.makemotivator.contracts;

import org.beryl.app.RegisterableContract;

public interface ICustomizerChangedEvent extends RegisterableContract {
	void onForecolorChanged(final int color);
}
