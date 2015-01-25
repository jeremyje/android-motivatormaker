package com.futonredemption.makemotivator.widget;

import android.app.Activity;
import android.app.ProgressDialog;

public class DetachableProgressDialog {
	Activity activity = null;
	int titleRdotId;
	int descriptionRdotId;
	boolean currentlyShowing = false;
	ProgressDialog dialog = null;

	public void onAttach(Activity activity) {
		this.activity = activity;
		attemptShow();
	}

	public void onDetach() {
		this.activity = null;
		attemptHide();
	}

	public void show(int titleRdotId, int descriptionRdotId) {
		this.titleRdotId = titleRdotId;
		this.descriptionRdotId = descriptionRdotId;
		currentlyShowing = true;
		attemptShow();
	}

	private void attemptShow() {
		if(activity != null && currentlyShowing) {
			dialog = new ProgressDialog(activity);
			dialog.setIndeterminate(true);
			dialog.setTitle(titleRdotId);
			dialog.setMessage(activity.getText(descriptionRdotId));
			dialog.setCancelable(false);
			dialog.show();
		}
	}

	public void dismiss() {
		currentlyShowing = false;
		attemptHide();
	}

	private void attemptHide() {
		if(dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}
}