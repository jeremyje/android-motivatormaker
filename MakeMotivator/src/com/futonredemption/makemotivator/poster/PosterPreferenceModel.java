package com.futonredemption.makemotivator.poster;

import com.futonredemption.makemotivator.Constants;

import android.content.Context;
import android.content.SharedPreferences;

public class PosterPreferenceModel {
	public String FileFormat;
	public String PosterSize;
	public String PosterTheme;
	
	public static PosterPreferenceModel loadPreferences(Context context) {
		final PosterPreferenceModel model = new PosterPreferenceModel();
		final SharedPreferences prefs = context.getSharedPreferences(Constants.Preferences.ApplicationKey, Context.MODE_PRIVATE);
		model.FileFormat = prefs.getString(Constants.Preferences.Key_FileFormat, Constants.Preferences.DefaultValue_FileFormat);
		model.PosterSize = prefs.getString(Constants.Preferences.Key_PosterSize, Constants.Preferences.DefaultValue_PosterSize);
		model.PosterTheme = prefs.getString(Constants.Preferences.Key_PosterTheme, Constants.Preferences.DefaultValue_PosterTheme);
		return model;
	}
}
