package com.futonredemption.makemotivator.activities;

import com.futonredemption.makemotivator.R;
import com.futonredemption.makemotivator.services.WebGalleryUploadService;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class WebGalleryUploadActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final Intent intent = this.getIntent();
		
		if(intent != null) {
			startUploadService(intent);
		}
		this.finish();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		
	}
	
	private void startUploadService(final Intent intent) {
		Intent startUpload = new Intent(this, WebGalleryUploadService.class);
		
		startUpload.putExtra(Intent.EXTRA_STREAM, (Uri)intent.getParcelableExtra(Intent.EXTRA_STREAM));
		startUpload.putExtra("title", intent.getStringExtra("title"));
		startUpload.putExtra("subtitle", intent.getStringExtra("subtitle"));
		startUpload.putExtra("frameColor", intent.getIntExtra("frameColor", Color.BLACK));
		startUpload.putExtra("filePath", intent.getStringExtra("filePath"));
		startUpload.putExtra("taskaction", "newupload");
		Toast.makeText(this, R.string.uploading_poster, Toast.LENGTH_LONG).show();
		this.startService(startUpload);
	}
}
