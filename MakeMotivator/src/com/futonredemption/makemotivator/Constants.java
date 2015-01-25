package com.futonredemption.makemotivator;

import android.graphics.Bitmap.CompressFormat;

public class Constants {

	public static class Application {
		public static final String PackageName = "com.futonredemption.makemotivator";
		public static final String AnalyticsId = "UA-26096822-1";
		//public static final String WebGalleryUrl = "http://192.168.1.20:8888";
		public static final String WebGalleryUrl = "https://motivatorposters.appspot.com";
		public static final String WebGalleryHomePageUrl = WebGalleryUrl + "/index.html";
	}

	public static class Preferences {
		public static final String ApplicationKey = "com.futonredemption.makemotivator.preferences";
		public static final String BackupAgentKey = "com.futonredemption.makemotivator.backupstore.preferences";
		
		public static final String TransientKey = "com.futonredemption.makemotivator.transient.preferences";
		
		public static final String Key_FileFormat = "PreferenceKey_FileFormat";
		public static final String DefaultValue_FileFormat = CompressFormat.JPEG.name();
		
		public static final String Key_PosterTheme = "PreferenceKey_PosterTheme";
		public static final String DefaultValue_PosterTheme = "fancy";
		
		public static final String Key_PosterSize = "PreferenceKey_PosterSize";
		public static final String DefaultValue_PosterSize = "large";
		
		public static final String Key_Analytics = "PreferenceKey_Analytics";
		public static final boolean DefaultValue_Analytics = true;
		
		public static final String Key_WebGalleryClientId = "PreferenceKey_WebGalleryClientId";
		
		public static final String Internal_FirstRun = "Internal_FirstRun";
	}
	
	public static class Exif {
		public static final String MarketLink = "http://goo.gl/9KekN";
		public static final String Make = "Motivator Maker for Android " + MarketLink;
		public static final String Aperture = "Android";
	}
	
	public static class Notifications {
		public static final int UploadPosterId = 1;
	}
}
