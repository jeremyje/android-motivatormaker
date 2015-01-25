package com.futonredemption.makemotivator.poster;

import android.graphics.Paint.FontMetrics;

public class AdvancedFontMetrics extends FontMetrics {
	/*
	public static float getMaxTextSize(String text, Paint p, Rect region) {
		return getMaxTextSize(text, p, region.width(), region.height(), 1);
	}

	public static float getMaxTextSize(String text, Paint p, float widthPixels, float heightPixels, int numLines) {
		float oldSize = p.getTextSize();
		float size = oldSize;
		float width;
		float height;
		float increment = 0.1F;
		Paint.FontMetrics metrics = new Paint.FontMetrics();

		if(widthPixels > 1 && heightPixels > 1) {
			do {
				size += increment;
				p.setTextSize(size);
				width = p.measureText(text);
				p.getFontMetrics(metrics);
				height = getTextHeight(p);
			} while (width < widthPixels && height < heightPixels);

			do {
				size -= increment;
				p.setTextSize(size);
				width = p.measureText(text);
				p.getFontMetrics(metrics);
				height = getTextHeight(p);
			} while (width > widthPixels || height > heightPixels);

			p.setTextSize(oldSize);
		}

		return size;
	}
	*/
	public float getTextHeight() {
		return 0 * this.bottom - this.top; // Works without underlining. (Remove +bottom for all CAPS)
	}

	public float getUnderlinedTextHeight() {
		return Math.abs(this.top + this.bottom) + this.bottom; // I get the underline height with this but it's shifted down some.
	}
}
