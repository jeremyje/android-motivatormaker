package com.futonredemption.makemotivator.poster;

import android.annotation.TargetApi;
import android.graphics.Bitmap.CompressFormat;

public class PreferenceValueConvert {
	public static float getScaleFactor(final PosterPreferenceModel model) {
		return getScaleFactor(model.PosterSize);
	}
	
	public static float getScaleFactor(final String posterSize) {
		final String sizeString = posterSize.toLowerCase();
		float scaleFactor = 1.0F;
		
		if(sizeString.equals("small")) {
			scaleFactor = 1.0F;
		} else if(sizeString.equals("medium")) {
			scaleFactor = 2.0F;
		} else if(sizeString.equals("large")) {
			scaleFactor = 3.0F;
		} else if(sizeString.equals("4x")) {
			scaleFactor = 4.0F;
		} else if(sizeString.equals("5x")) {
			scaleFactor = 5.0F;
		} else if(sizeString.equals("max")) {
			scaleFactor = 15.0F;
		}
		
		return scaleFactor;
	}

	@TargetApi(14)
	public static CompressFormat getCompressFormat(final String fileFormat) {
		CompressFormat outputFormat = CompressFormat.PNG;
		if(fileFormat.equalsIgnoreCase("png")) {
			outputFormat = CompressFormat.PNG;
		} else if(fileFormat.equalsIgnoreCase("webp")) {
			outputFormat = CompressFormat.WEBP;
		} else {
			outputFormat = CompressFormat.JPEG;
		}
		return outputFormat;
	}
}
