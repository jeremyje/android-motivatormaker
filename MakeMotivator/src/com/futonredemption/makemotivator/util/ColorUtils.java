package com.futonredemption.makemotivator.util;

import java.security.SecureRandom;

import org.beryl.app.AndroidVersion;
import org.beryl.graphics.BitmapWrapper;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.FloatMath;

public class ColorUtils {

	private static final SecureRandom random = new SecureRandom();

	private static int getSampleSize() {

		// Use smaller sample for non-JIT versions since this has a lot of JNI overhead.
		if(AndroidVersion.isFroyoOrHigher()) {
			return 256;
		} else {
			return 64;
		}
	}

	public static int brightenIfTooDark(int color) {
		final float [] hsvColor = new float[3];
		Color.colorToHSV(color, hsvColor);

		hsvColor[2] = Math.max(hsvColor[2], 0.5f);
		color = Color.HSVToColor(hsvColor);

		return color;
	}


	// Spelled out for efficiency.
	public static boolean isShadeOfGray(int red, int green, int blue) {
		return Math.abs(Math.max(red, Math.max(green, blue)) - Math.min(red, Math.min(green, blue))) < 20;
	}

	public static int findDominantColor(Bitmap bitmap, Cancellable cancelcheck) {

		final int origWidth = bitmap.getWidth();
		final int origHeight = bitmap.getHeight();
		int scaleWidth;
		int scaleHeight;

		if(origWidth > origHeight) {
			scaleWidth = getSampleSize();
			scaleHeight = (int) (scaleWidth * ((float)origHeight / origWidth));
		} else {
			scaleHeight = getSampleSize();
			scaleWidth = (int) (scaleHeight * ((float)origWidth / origHeight));
		}

		final DominantColorAccumulator accum = new BucketBasedColorAccumulator();

		if(! bitmap.isRecycled()) {
			final Bitmap sampleBitmap = Bitmap.createScaledBitmap(bitmap, scaleWidth, scaleHeight, true);
			final int width = sampleBitmap.getWidth();
			final int height = sampleBitmap.getHeight();

			for(int i = 0; i < width; i++) {
				if(! cancelcheck.isCancelled()) {
					for(int j = 0; j < height; j++) {
						final int color = sampleBitmap.getPixel(i, j);
						accum.addColor(color);
					}
				}
			}
			BitmapWrapper.dispose(sampleBitmap);
		}

		return accum.getColor();
	}

	public static int randomColor() {
		return Color.argb(100, random.nextInt(256), random.nextInt(256), random.nextInt(256));
	}

	interface DominantColorAccumulator {
		void addColor(int color);
		int getColor();
	}


	static class ColorBucket {
		public final int baseHue;
		private int affinityPoints = 0;
		private final float bucketDegreesBy100;
		int numAccum = 0;

		int redAccum = 0;
		int greenAccum = 0;
		int blueAccum = 0;

		public ColorBucket(int baseHue, float bucketDegrees) {
			this.baseHue = baseHue;
			this.bucketDegreesBy100 = 100.0f / bucketDegrees;
		}

		public int getAffinity(int hue) {
			return 100 - (int)(bucketDegreesBy100 * (Math.abs(baseHue - hue) % 360));
		}

		public void addColor(int red, int green, int blue, int hue) {
			final int affinity = getAffinity(hue);
			if(affinity > 25) {
				redAccum += red;
				greenAccum += green;
				blueAccum += blue;
				numAccum++;
				affinityPoints += affinity;
			}
		}

		public int getAffinityPoints() {
			return affinityPoints;
		}

		public int getColor() {
			if(numAccum == 0) {
				numAccum = 1;
			}
			return Color.rgb(redAccum / numAccum, greenAccum / numAccum, blueAccum / numAccum);
		}
	}

	static class BucketBasedColorAccumulator implements DominantColorAccumulator {

		final int numBuckets;
		final float degreesPerBucket;
		public final ColorBucket [] buckets;
		final float[] currentHSV = new float[3];

		public BucketBasedColorAccumulator() {
			numBuckets = 7;
			degreesPerBucket = 360.0f / numBuckets;

			buckets = new ColorBucket[numBuckets];
			for(int i = 0; i < numBuckets; i++) {
				buckets[i] = new ColorBucket((int)degreesPerBucket * i, degreesPerBucket);
			}
		}

		public void addColor(final int color) {
			Color.colorToHSV(color, currentHSV);
			// Don't count bland colors.
			if(currentHSV[1] > 0.5f && currentHSV[2] > 0.5f) {

				addToBucket(color);
			}
		}

		private void addToBucket(final int color) {
			final int red = Color.red(color);
			final int green = Color.green(color);
			final int blue = Color.blue(color);
			final int hue = (int) currentHSV[0];

			// Getting the hi/lo color buckets.
			final float exactIndex = currentHSV[0] / degreesPerBucket;
			final int lowIndex = (int) FloatMath.floor(exactIndex) % numBuckets;
			final int highIndex = (int) FloatMath.ceil(exactIndex) % numBuckets;
			final ColorBucket highBucket = buckets[highIndex];
			final ColorBucket lowBucket = buckets[lowIndex];

			highBucket.addColor(red, green, blue, hue);
			if(highBucket != lowBucket) {
				lowBucket.addColor(red, green, blue, hue);
			}
		}

		public int getColor() {
			int dominantBucketIndex = 0;
			int highestAffinityPoints = 0;
			int affinityPoints;
			for(int i = 0; i < numBuckets; i++) {
				affinityPoints = buckets[i].getAffinityPoints();
				if(affinityPoints > highestAffinityPoints) {
					highestAffinityPoints = affinityPoints;
					dominantBucketIndex = i;
				}
			}

			return buckets[dominantBucketIndex].getColor();
		}
	}

	static class RgbColorAccumulator implements DominantColorAccumulator {

		int numAccum = 0;

		int redAccum = 0;
		int greenAccum = 0;
		int blueAccum = 0;

		public void addColor(int color) {
			final int red = Color.red(color);
			final int green = Color.green(color);
			final int blue = Color.blue(color);

			if(! isShadeOfGray(red, green, blue)) {
				redAccum += red;
				greenAccum += green;
				blueAccum += blue;
				numAccum++;
			}
		}

		private boolean isShadeOfGray(int red, int green, int blue) {
			final int min = Math.min(red, Math.min(green, blue));
			final int max = Math.max(red, Math.max(green, blue));

			return Math.abs(max - min) < 20;
		}

		public int getColor() {
			if(numAccum == 0) {
				numAccum = 1;
			}
			return Color.rgb(redAccum / numAccum, greenAccum / numAccum, blueAccum / numAccum);
		}
	}

	static class HsvColorAccumulator implements DominantColorAccumulator {

		//private static final int H = 0;
		private static final int S = 1;
		private static final int V = 2;

		final float [] hsvAccum = new float[] { 0.0F, 0.0F, 0.0F };
		final float [] hsvSet = new float[] { 0.0F, 0.0F, 0.0F };
		final int size = hsvAccum.length;
		int numAccum = 0;

		public void addColor(final int color) {
			Color.colorToHSV(color, hsvSet);

			if(! isShadeOfGray(hsvSet)) {
				for(int i = 0; i < size; i++) {
					hsvAccum[i] += hsvSet[i];
				}
				numAccum++;
			}
		}

		private boolean isShadeOfGray(final float[] hsvSet) {
			if(hsvSet[S] < 0.2f || hsvSet[V] < 0.2f) {
				return true;
			} else {
				return false;
			}
		}

		public int getColor() {
			final float [] hsvResult = hsvAccum.clone();

			for(int i = 0; i < size; i++) {
				hsvResult[i] += hsvResult[i] / numAccum;
			}

			return Color.HSVToColor(hsvResult);
		}
	}
}
