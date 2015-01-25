package com.futonredemption.makemotivator.poster.fancy;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.futonredemption.makemotivator.poster.AbstractTextPosterElement;
import com.futonredemption.makemotivator.poster.measure.MeasureParams;
import com.futonredemption.makemotivator.util.ColorUtils;
import com.futonredemption.makemotivator.util.FontRenderer;
import com.futonredemption.makemotivator.util.FontUtils;

public class TitleElement extends AbstractTextPosterElement {

	public TitleElement() {
		super(Color.WHITE, FontUtils.getCaslon540Bt());
	}

	@Override
	public Rect getBaseMeasurements(MeasureParams params) {
		if(params.orientation == MeasureParams.ORIENTATION_Landscape) {
			return new Rect(50, 281, 456, 316);
		} else {
			return new Rect(49, 348, 276, 384);
		}
	}

	//http://b.ivity.asia/2010/12/29/using-android-text-staticlayout/
	//http://blog.stylingandroid.com/archives/177
	//http://stackoverflow.com/questions/2159847/is-there-any-example-about-spanned-and-spannable-text
	@Override
	protected void onDraw(Canvas canvas, Rect region) {
		final String renderedText = text.toUpperCase();
		final String firstChar = renderedText.substring(0, 1);
		final String innerString = renderedText.substring(1, renderedText.length() - 1);
		final String lastChar = renderedText.substring(renderedText.length() - 1);

		float size;
		Paint.FontMetrics outerMetrics = new Paint.FontMetrics();
		final TextPaint outerPaint = createPaintForText();
		size = FontUtils.getMaxTextSizeUpperCase(innerString, outerPaint, region);
		outerPaint.setTextSize(size);
		outerPaint.getFontMetrics(outerMetrics);
		outerPaint.setTextAlign(Align.LEFT);


		float bottom = region.top + Math.abs(outerMetrics.ascent) - Math.abs(outerMetrics.descent);

		Rect innerRegion = new Rect(region);
		innerRegion.bottom = (int)bottom;

		Paint.FontMetrics innerMetrics = new Paint.FontMetrics();
		final TextPaint innerPaint = createPaintForText();
		size = FontUtils.getMaxTextSizeUpperCase(innerString, innerPaint, innerRegion);
		innerPaint.setTextSize(size * 0.75F);
		innerPaint.getFontMetrics(innerMetrics);
		innerPaint.setTextAlign(Align.LEFT);

		StaticLayout firstDraw = new StaticLayout(firstChar, outerPaint, region.width(), Layout.Alignment.ALIGN_NORMAL, 1.0F, 1.0F, false);
		StaticLayout middleDraw = new StaticLayout(innerString, innerPaint, region.width(), Layout.Alignment.ALIGN_NORMAL, 1.0F, 1.0F, false);
		StaticLayout lastDraw = new StaticLayout(lastChar, outerPaint, region.width(), Layout.Alignment.ALIGN_NORMAL, 1.0F, 1.0F, false);

		final float NUDGE = Math.abs(outerPaint.measureText("I") - outerPaint.measureText("!")); // This isn't so great but it works well enough.
		float totalWidth = outerPaint.measureText(firstChar) + NUDGE + innerPaint.measureText(innerString) + NUDGE + outerPaint.measureText(lastChar);

		float left = region.left + (region.right - region.left) / 2 - totalWidth / 2;

		canvas.save();
		canvas.translate(left, region.top);
		canvas.translate(0, 0.0F - outerMetrics.bottom);
		firstDraw.draw(canvas);
		canvas.translate(NUDGE, outerMetrics.bottom - innerMetrics.bottom);
		canvas.translate(outerPaint.measureText(firstChar), 0);
		middleDraw.draw(canvas);
		canvas.translate(NUDGE, innerMetrics.bottom - outerMetrics.bottom);
		canvas.translate(innerPaint.measureText(innerString), 0);
		lastDraw.draw(canvas);
		canvas.restore();

		Paint linePaint = new Paint();
		linePaint.setAntiAlias(true);
		linePaint.setColor(foreColor);
		linePaint.setStrokeWidth(2);

		canvas.drawLine(left + NUDGE + outerPaint.measureText(firstChar), bottom - linePaint.getStrokeWidth(),
				left + NUDGE + outerPaint.measureText(firstChar) + innerPaint.measureText(innerString), bottom - linePaint.getStrokeWidth(),
				linePaint);
		if(isInDebugDrawMode()) {
			FontRenderer.FontMetricsPainter fmp = new FontRenderer.FontMetricsPainter(canvas, region);
			fmp.drawMetrics(outerMetrics, innerMetrics);
		}
	}

	static class LinePrinter {

		final Rect region;
		float startX;
		final Canvas canvas;
		public LinePrinter(Canvas canvas, Rect region) {
			this.region = region;
			this.canvas = canvas;
			this.startX = region.left;
		}

		public void drawMetrics(Paint.FontMetrics outerMetrics, Paint.FontMetrics innerMetrics) {
			printLine("ascent", outerMetrics.ascent);
			printLine("bottom", outerMetrics.bottom);
			printLine("descent", outerMetrics.descent);
			printLine("leading", outerMetrics.leading);
			printLine("top", outerMetrics.top);

			printLine("i-ascent", innerMetrics.ascent);
			printLine("i-bottom", innerMetrics.bottom);
			printLine("i-descent", innerMetrics.descent);
			printLine("i-leading", innerMetrics.leading);
			printLine("i-top", innerMetrics.top);
		}

		public void printLine(String name, float mag) {
			TextPaint p = new TextPaint();
			p.setTextSize(20.0F);
			p.setColor(ColorUtils.randomColor());
			p.setStrokeWidth(5.0F);
			canvas.drawLine(startX, region.top, startX, region.top - mag, p);
			canvas.drawText(name, startX, region.top - mag, p);
			startX += p.measureText(name) + 5.0F;
		}

	}

	@Override
	protected int getNumLines() {
		return 1;
	}
}
