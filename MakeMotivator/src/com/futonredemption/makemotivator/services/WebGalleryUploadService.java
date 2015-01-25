package com.futonredemption.makemotivator.services;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.beryl.app.ComponentEnabler;
import org.beryl.intents.android.Web;

import com.futonredemption.makemotivator.Constants;
import com.futonredemption.makemotivator.receivers.DeferredUploadTripBroadcastReceiver;
import com.futonredemption.makemotivator.webupload.PosterUploadClient;
import com.futonredemption.makemotivator.webupload.PosterUploadWorkTaskModel;
import com.futonredemption.makemotivator.webupload.UploadNotification;
import com.futonredemption.makemotivator.webupload.WorkQueue;

import android.app.IntentService;
import android.content.Intent;

public class WebGalleryUploadService extends IntentService {

	private WorkQueue queue = new WorkQueue(this);
	
    public WebGalleryUploadService() {
        super("WebGalleryUploadService");
    }
    
    public WebGalleryUploadService(String name) {
        super(name);
        Thread.currentThread().setName(name);
    }
    
    @Override
	protected void onHandleIntent(Intent intent) {
    	if(isNewTask(intent)) {
    		queue.addTask(intent);
    	} else {
    		disableDeferredUpload();
    	}
    	workAllTasks();
    }
    
    private void workAllTasks() {
    	queue.load();
    	try {
	    	while(! queue.isEmpty()) {
	    		PosterUploadWorkTaskModel model = queue.getNext();
    	        try {
    				uploadPoster(model);
    				queue.currentTaskCompleted();
    			} catch (Exception e) {
    				stopAttemptsAndWaitForABetterTime();
    			}
	    	}
    	} catch(Exception e) {
    		// Catch rethrow by stopAttemptsAndWaitForABetterTime to abort loop.
    	}
    	queue.save();
    }
    
    private void stopAttemptsAndWaitForABetterTime() {
    	enableDeferredUpload();
    	throw new RuntimeException("Abandon request");
	}
    
    private void enableDeferredUpload() {
    	ComponentEnabler enabler = new ComponentEnabler(this);
    	enabler.enable(DeferredUploadTripBroadcastReceiver.class);
    }
    
    private void disableDeferredUpload() {
    	ComponentEnabler enabler = new ComponentEnabler(this);
    	enabler.disable(DeferredUploadTripBroadcastReceiver.class);
    }

	private boolean isNewTask(final Intent intent) {
    	final String value = intent.getStringExtra("taskaction");
    	return value != null && value.equalsIgnoreCase("newupload");
    }

    public void uploadPoster(final PosterUploadWorkTaskModel model) throws Exception {
    	
		final PosterUploadClient client = new PosterUploadClient();

		// Load poster into bytearray.
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		final InputStream in = this.getContentResolver().openInputStream(model.contentUri);
		final byte[] buffer = new byte[8196]; // you can configure the
		// buffer size
		int readBytes = 0;
		while ((readBytes = in.read(buffer)) != -1) {
			out.write(buffer, 0, readBytes);
		}
		String jsonResponse;
		String clientId = client.getClientId(this);
		jsonResponse = client.execute(model, clientId, out.toByteArray());

		try {
			String website = Constants.Application.WebGalleryHomePageUrl;
	    	try {
	    		website = client.getUrl(jsonResponse);
	    	} catch(Exception e) {
	    		website = Constants.Application.WebGalleryHomePageUrl;
	    	}
	    	final Intent viewImageOnWeb = Web.viewUrl(website);
	    	final UploadNotification notify = new UploadNotification(this);
	    	notify.uploadCompleted(model.title, viewImageOnWeb);
		} catch(Exception e) {
			// If anything fails after receiving the response then don't bother.
		}
    }
}
