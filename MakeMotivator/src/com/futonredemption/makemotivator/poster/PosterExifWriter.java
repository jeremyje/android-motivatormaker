package com.futonredemption.makemotivator.poster;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.beryl.app.AndroidVersion;

import com.futonredemption.makemotivator.Constants;

import android.annotation.TargetApi;
import android.media.ExifInterface;

public class PosterExifWriter {

	public void writeTags(IPosterComposition composition, String absolutePath) {
		IExifWriter writer;
		
		if(AndroidVersion.isEclairOrHigher()) {
			writer = new EclairExifWriter();
		} else {
			writer = new BeforeEclairExifWriter();
		}
		writer.writeTags(composition, absolutePath);
	}
	
	static interface IExifWriter {
		void writeTags(IPosterComposition composition, String absolutePath);
	}
	
	static class BeforeEclairExifWriter implements IExifWriter {
		public void writeTags(IPosterComposition composition, String absolutePath) {
		}
	}
	
	@TargetApi(5)
	static class EclairExifWriter implements IExifWriter {

		public void writeTags(IPosterComposition composition, String absolutePath) {
			try {
				SimpleDateFormat formatter;
				formatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
				formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
				
				ExifInterface exif = new ExifInterface(absolutePath);
				Date date = new Date();
				String formattedDateTime = formatter.format(date);
				exif.setAttribute(ExifInterface.TAG_APERTURE, Constants.Exif.Aperture);
				exif.setAttribute(ExifInterface.TAG_DATETIME, formattedDateTime);
				exif.setAttribute(ExifInterface.TAG_ISO, Constants.Exif.MarketLink);
				exif.setAttribute(ExifInterface.TAG_MAKE, Constants.Exif.Make);
				exif.setAttribute(ExifInterface.TAG_MODEL, composition.getTitle());
				exif.setAttribute(ExifInterface.TAG_ORIENTATION, Integer.toString(ExifInterface.ORIENTATION_NORMAL));
				exif.setAttribute(ExifInterface.TAG_WHITE_BALANCE, Integer.toString(ExifInterface.WHITEBALANCE_AUTO));
				exif.saveAttributes();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
