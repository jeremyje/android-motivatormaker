package com.futonredemption.makemotivator.poster;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.beryl.graphics.BitmapProperties;
import org.beryl.graphics.BitmapWrapper;
import org.beryl.io.DirectoryUtils;
import org.beryl.util.Memory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.futonredemption.makemotivator.poster.measure.MeasureParams;
import com.futonredemption.makemotivator.util.FileUtils;
import com.futonredemption.makemotivator.util.StringUtils;

public class PosterRenderer {

	public static File savePoster(Context context, IPosterComposition composition, String fileName) throws IOException {
		String posterFileName = fileName.trim();
		CompressFormat outputFormat = CompressFormat.JPEG;
		
		// Don't let the file name go beyond 100 characters.
		posterFileName = posterFileName.substring(0, Math.min(posterFileName.length(), 100));
		posterFileName = StringUtils.alphaNumeric(posterFileName);
		posterFileName = StringUtils.capitalizeFirstLetterOfEachWord(posterFileName);

		if(posterFileName.length() < 1) {
			posterFileName = "Poster";
		}

		final File libraryDir = DirectoryUtils.getPublicPictureLibrary("Motivators");
		final File posterFile = FileUtils.safeCreateFilePath(libraryDir, posterFileName + "." + composition.getFileFormat());
		
		Bitmap poster = PosterRenderer.create(composition);

		final OutputStream outputStream = new FileOutputStream(posterFile);
		
		outputFormat = PreferenceValueConvert.getCompressFormat(composition.getFileFormat());

		poster.compress(outputFormat, 100, outputStream);
		outputStream.close();
		
		final PosterExifWriter exifWriter = new PosterExifWriter();
		exifWriter.writeTags(composition, posterFile.getAbsolutePath());

		BitmapWrapper.dispose(poster); // Force finalization of the bitmap since it's already saved to disk.
		poster = null;

		return posterFile;
	}

	public static File savePoster(Context context, IPosterComposition composition) throws IOException {
		return savePoster(context, composition, composition.getTitle());
	}

	public static Bitmap create(final IPosterComposition composition) {
		PosterFactoryResolver resolver = new PosterFactoryResolver();
		
		final IPosterElementFactory posterTheme = resolver.getFactory(composition.getThemeTag());
		final PosterFactory posterFactory = new PosterFactory(posterTheme);
		final AbstractPosterElement root = posterFactory.create(composition);

		MeasureParams params = composition.getMeasureParams();
		
		params.scale = getSafeScaleFactor(composition, root, params);
		root.measure(params);

		Bitmap picture = null;
		
		do {
			try {
				picture = Bitmap.createBitmap(root.getWidth(), root.getHeight(), Config.ARGB_8888);
			} catch(OutOfMemoryError e) {
				picture = null;
				params.scale -= 1.0F;
				root.measure(params);
				
				if(params.scale < 0.0F) {
					throw e;
				}
			}
		} while(picture == null);
		
		Canvas canvas = new Canvas(picture);

		root.draw(canvas);

		return picture;
	}

	private static float getSafeScaleFactor(IPosterComposition composition,
			AbstractPosterElement root, MeasureParams params) {
		float scaleFactor = composition.getSizeMultiplier();
		final Rect baseSize = root.getBaseMeasurements(params);
		final long baseWidth = baseSize.width();
		final long baseHeight = baseSize.height();
		long imageBytes;
		final long safeBytes = Memory.getSizeLimit(2);
		
		scaleFactor += 0.1F;
		
		do {
			scaleFactor -= 0.1F;
			imageBytes = BitmapProperties.getMemoryFootprintForRgba8888(
					(long)(baseWidth * scaleFactor), 
					(long)(baseHeight * scaleFactor));
		} while(safeBytes < imageBytes && scaleFactor > 1.0F);

		return scaleFactor;
	}
}
