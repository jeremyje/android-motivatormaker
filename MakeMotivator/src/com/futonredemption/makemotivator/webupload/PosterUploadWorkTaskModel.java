package com.futonredemption.makemotivator.webupload;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class PosterUploadWorkTaskModel {
	public Uri contentUri;
    public String title;
    public String subtitle;
    public int frameColor;
    public String filePath;
    
	public void setBundleParams(final Bundle bundle) {
        bundle.putParcelable("contentUri", this.contentUri);
        bundle.putString("title", this.title);
        bundle.putString("subtitle", this.subtitle);
        bundle.putInt("frameColor", this.frameColor);
        bundle.putString("filePath", this.filePath);
    }
    
    public void fromBundleParams(final Bundle bundle) {
        this.contentUri = bundle.getParcelable(Intent.EXTRA_STREAM);
        this.title = bundle.getString("title");
        this.subtitle = bundle.getString("subtitle");
        this.frameColor = bundle.getInt("frameColor");
        this.filePath = bundle.getString("filePath");
    }
    
    public static JSONObject toJson(PosterUploadWorkTaskModel model) throws JSONException {
    	JSONObject json = new JSONObject();
    	json.put("contentUri", model.contentUri);
    	json.put("title", model.title);
    	json.put("subtitle", model.subtitle);
    	json.put("frameColor", model.frameColor);
    	json.put("filePath", model.filePath);
    	return json;
    }
    
    public static PosterUploadWorkTaskModel fromJson(JSONObject json) throws JSONException {
    	PosterUploadWorkTaskModel model = new PosterUploadWorkTaskModel();
    	model.contentUri = Uri.parse(json.getString("contentUri"));
    	model.title = json.getString("title");
    	model.subtitle = json.getString("subtitle");
    	model.frameColor = json.getInt("frameColor");
    	model.filePath = json.getString("filePath");
    	return model;
    }
}
