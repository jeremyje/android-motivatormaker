package com.futonredemption.makemotivator.contracts;

import org.beryl.app.RegisterableContract;

public interface IImageAcquireCommand extends RegisterableContract {
	public void beginAcquireByGallery();
	public void beginAcquireByCamera();
}
