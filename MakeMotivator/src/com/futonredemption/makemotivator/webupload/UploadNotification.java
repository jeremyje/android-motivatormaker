package com.futonredemption.makemotivator.webupload;

import java.security.SecureRandom;

import com.futonredemption.makemotivator.R;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class UploadNotification {

	private static SecureRandom random = new SecureRandom();
	private final Context context;
	private final NotificationManager notifyManager;
	private final NotificationCompat.Builder builder;
	
	public UploadNotification(Context context) {
		this.context = context;
		this.notifyManager = (NotificationManager)this.context.getSystemService(Service.NOTIFICATION_SERVICE);
		this.builder = new NotificationCompat.Builder(this.context);
	}

	public void uploadCompleted(CharSequence title, Intent action) {
		this.builder.setTicker(getText(R.string.upload_complete))
			.setContentText(getText(R.string.click_to_view_image))
	    	.setContentTitle(title + " " + getText(R.string.upload_complete))
	    	.setSmallIcon(android.R.drawable.stat_sys_upload_done)
	    	.setAutoCancel(true)
	    	.setOngoing(false);
    	PendingIntent futureWebIntent = PendingIntent.getActivity(this.context, 0, action, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
    	this.builder.setContentIntent(futureWebIntent);
    	
    	show(random.nextInt());
	}

	private void show(int id) {
		this.notifyManager.notify(id, builder.getNotification());
	}
	
	private CharSequence getText(int resId) {
		return this.context.getText(resId);
	}
}
