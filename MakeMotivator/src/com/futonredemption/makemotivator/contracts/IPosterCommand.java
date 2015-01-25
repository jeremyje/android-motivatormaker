package com.futonredemption.makemotivator.contracts;

import org.beryl.app.RegisterableContract;

public interface IPosterCommand extends RegisterableContract {
	void invokeShareDialog();
	void sharePoster();
	void sharePoster(String packageName, String activityName);
	void savePoster();
	void uploadPoster();
	void rotatePicture();
	void takePictureFromCamera();
	void selectPictureFromGallery();
}
