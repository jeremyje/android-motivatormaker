package com.futonredemption.makemotivator.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

public class FontUtils {

	private static Typeface Caslon540Bt = null;

	private static final float MIN_SIZE = 12.0F;
	private static final float MAX_SIZE = 512.0F;
	
	public static Typeface getCaslon540Bt() {
		return Caslon540Bt;
	}
	
	public static Typeface getCaslon540Bt(final Context context) {
		if(Caslon540Bt == null) {
			Caslon540Bt = loadFont(context, "typeface/caslon_540_bt.ttf");
		}
		return Caslon540Bt;
	}

	private static Typeface loadFont(final Context context, final String assetPath) {
		final AssetManager assets = context.getAssets();
		final Typeface loadedFont = Typeface.createFromAsset(assets, assetPath);
		return loadedFont;
	}

	public static float getMaxTextSizeUpperCase(String text, Paint p, Rect region) {
		float oldSize = p.getTextSize();
		float size = oldSize;
		float width;
		float height;
		float increment = 0.1F;
		Paint.FontMetrics metrics = new Paint.FontMetrics();
		final float regionWidth = region.width();
		final float regionHeight = region.height();

		if(regionWidth > 1 && regionHeight > 1) {
			do {
				size += increment;
				p.setTextSize(size);
				width = p.measureText(text);
				p.getFontMetrics(metrics);
				height = getTextHeightUpperCase(p, metrics);
			} while (width < regionWidth && height < regionHeight && size <= MAX_SIZE);

			do {
				size -= increment;
				p.setTextSize(size);
				width = p.measureText(text);
				p.getFontMetrics(metrics);
				height = getTextHeightUpperCase(p, metrics);
			} while ((width > regionWidth || height > regionHeight) && size > MIN_SIZE);

			p.setTextSize(oldSize);
		}

		return size;
	}

	public static float getMaxTextSize(String text, Paint p, Rect region) {
		float oldSize = p.getTextSize();
		float size = oldSize;
		float width;
		float height;
		float increment = 0.1F;
		Paint.FontMetrics metrics = new Paint.FontMetrics();
		final float regionWidth = region.width();
		final float regionHeight = region.height();

		if(regionWidth > 1 && regionHeight > 1) {
			do {
				size += increment;
				p.setTextSize(size);
				width = p.measureText(text);
				p.getFontMetrics(metrics);
				height = getTextHeight(p, metrics);
			} while (width < regionWidth && height < regionHeight);

			do {
				size -= increment;
				p.setTextSize(size);
				width = p.measureText(text);
				p.getFontMetrics(metrics);
				height = getTextHeight(p, metrics);
			} while (width > regionWidth || height > regionHeight);

			p.setTextSize(oldSize);
		}

		return size;
	}

	private static float getTextHeight(Paint p, Paint.FontMetrics metrics) {
		float height;

		//height = Math.abs(metrics.ascent + metrics.descent + metrics.leading);

		if(p.isUnderlineText()) {
			height = Math.abs(metrics.top + metrics.bottom) + metrics.bottom; // I get the underline height with this but it's shifted down some.
		} else {
			height = 0 * metrics.bottom - metrics.top; // Works without underlining. (Remove +bottom for all CAPS)
		}

		return height;
	}

	private static float getTextHeightUpperCase(Paint p, Paint.FontMetrics metrics) {
		float height;

		//height = Math.abs(metrics.ascent + metrics.descent + metrics.leading);

		if(p.isUnderlineText()) {
			height = Math.abs(metrics.top + metrics.bottom) + metrics.bottom; // I get the underline height with this but it's shifted down some.
		} else {
			height = Math.abs(metrics.top + metrics.bottom); // Works without underlining. (Remove +bottom for all CAPS)
		}

		return height;
	}
}
