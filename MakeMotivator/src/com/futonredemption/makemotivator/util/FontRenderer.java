package com.futonredemption.makemotivator.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;

public class FontRenderer {

	// FIXME: What is this doing?
	public static float getRecommendedTextSize(String text, Paint p, Rect region) {
		float textSize = 10.0F;
		TextPaint tp = new TextPaint();
		tp.setTypeface(FontUtils.getCaslon540Bt(null));
		for(int i = 0; i < 1000; i++) {
			tp.setTextSize(i);
		}

		return textSize;
	}

	public static class FontMetricsPainter {
		final Rect region;
		float startX;
		float startY;
		final Canvas canvas;

		public FontMetricsPainter(Canvas canvas, Rect region) {
			this.region = region;
			this.canvas = canvas;
			this.startX = region.left;
		}

		public void drawMetrics(Paint.FontMetrics... metrics) {
			for(Paint.FontMetrics metric : metrics) {
				setBaseline(metric.top);
				printLine("ascent", metric.ascent);
				printLine("bottom", metric.bottom);
				printLine("descent", metric.descent);
				printLine("leading", metric.leading);
				printLine("top", metric.top);
				drawBaseline();
			}
		}

		private void drawBaseline() {
			TextPaint p = new TextPaint();
			p.setColor(ColorUtils.randomColor());
			p.setStrokeWidth(3.0F);
			canvas.drawLine(region.left, startY, startX, startY, p);
		}

		private void setBaseline(float topToBaseline) {
			startY = region.top - topToBaseline;
		}

		public void printLine(String name, float mag) {
			TextPaint p = new TextPaint();
			p.setTextSize(20.0F);
			p.setColor(ColorUtils.randomColor());
			p.setStrokeWidth(5.0F);
			canvas.drawLine(startX, startY, startX, startY + mag, p);
			canvas.drawText(name, startX, startY + mag, p);
			startX += p.measureText(name) + 5.0F;
		}
	}
}
