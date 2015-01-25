package com.futonredemption.makemotivator.webupload;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.futonredemption.makemotivator.Constants;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class WorkQueue {

	private final Context context;
	private List<PosterUploadWorkTaskModel> workQueue = new ArrayList<PosterUploadWorkTaskModel>();
	
	public WorkQueue(Context context) {
		this.context = context;
	}
	
	public void addTask(Intent intent) {
		load();
		PosterUploadWorkTaskModel model = new PosterUploadWorkTaskModel();
        model.fromBundleParams(intent.getExtras());
        workQueue.add(model);
        save();
	}
	
	public PosterUploadWorkTaskModel getNext() {
		return workQueue.get(0);
	}
	
	public boolean isEmpty() {
		return workQueue == null || workQueue.isEmpty();
	}
	
	public void currentTaskCompleted() {
		workQueue.remove(0);
		save();
	}
	
	public void load() {
		workQueue.clear();
		try {
			final String contents = loadContents();
			if(contents.length() > 0) {
				final JSONArray array = new JSONArray(contents);
				final int len = array.length();
				for(int i = 0; i < len; i++) {
					final JSONObject sitem = array.getJSONObject(i);
					workQueue.add(PosterUploadWorkTaskModel.fromJson(sitem));
				}
			}
		} catch(Exception e) {
			
		}
	}
	
	public void save() {
		try {
			final int len = workQueue.size();
			final JSONArray array = new JSONArray();
			for(int i = 0 ; i < len; i++) {
				array.put(i, PosterUploadWorkTaskModel.toJson(workQueue.get(i)));
			}
			saveContents(array.toString());
		} catch(Exception e) {
			
		}
	}
	
	private String loadContents() {
		SharedPreferences prefs = getPreferences();
		return prefs.getString("work-queue-json", "");
	}
	
	private void saveContents(String contents) {
		Editor edit = getPreferences().edit();
		edit.putString("work-queue-json", contents);
		edit.commit();
	}
	
	private SharedPreferences getPreferences() {
		return context.getSharedPreferences(Constants.Preferences.TransientKey, Context.MODE_PRIVATE);
	}
}
