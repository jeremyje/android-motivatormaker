package com.futonredemption.makemotivator.receivers;

import com.futonredemption.makemotivator.services.WebGalleryUploadService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DeferredUploadTripBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		final Intent startUpload = new Intent(context, WebGalleryUploadService.class);
		startUpload.putExtra("taskAction", "newUpload");
		context.startService(startUpload);
	}
}
