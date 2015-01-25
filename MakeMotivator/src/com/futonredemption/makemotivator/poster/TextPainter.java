package com.futonredemption.makemotivator.poster;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

public class TextPainter {

	private static final float FONTSIZE_Autosize = -1.0f;
	private float fontSize = FONTSIZE_Autosize;
	private Typeface font = Typeface.SANS_SERIF;
	private int numLines = 1;
	private int textColor = Color.WHITE;

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public TextPainter() {

	}

	public float getFontSize() {
		return fontSize;
	}

	public void setFontSize(float fontSize) {
		this.fontSize = fontSize;
	}

	public Typeface getFont() {
		return font;
	}

	public void setFont(Typeface font) {
		this.font = font;
	}

	public int getNumLines() {
		return numLines;
	}

	public void setNumLines(int numLines) {
		this.numLines = numLines;
	}

	protected TextPaint createPaintForText() {
		final TextPaint p = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		p.setAntiAlias(true);
		p.setColor(textColor);
		p.setTypeface(font);
		p.setStrokeWidth(1);
		int flags = p.getFlags();
		flags = flags & ~Paint.DEV_KERN_TEXT_FLAG;
		p.setFlags(flags);
		return p;
	}

	public void drawText(Canvas canvas, Rect region, String text) {
		final int width = region.width();
		final int height = region.height();
		final Rect clipRect = new Rect(0, 0, width, height);
		final TextPaint p = createPaintForText();
		float size = (float)height;
		size /= numLines;

		StaticLayout textRender = null;
		int renderHeight = 0;
		int lastLine = 0;
		do {
			p.setTextSize(size);
			textRender = new StaticLayout(text, p, width, Layout.Alignment.ALIGN_CENTER, 1.0f, 1.0f, true);
			size -= 0.1f;
			lastLine = textRender.getLineCount() - 1;
			renderHeight = textRender.getHeight() - textRender.getTopPadding() - textRender.getBottomPadding() - textRender.getLineDescent(lastLine);
		} while(renderHeight > height);

		int topAntiPadding = textRender.getTopPadding() + textRender.getLineDescent(lastLine);
		region.top -= topAntiPadding;
		clipRect.bottom += topAntiPadding;
		canvas.save();
		canvas.translate(region.left, region.top);
		canvas.clipRect(clipRect, Op.REPLACE);
		textRender.draw(canvas);
		canvas.restore();
	}
}
