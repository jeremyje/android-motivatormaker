package com.futonredemption.makemotivator.poster;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;

//http://android.git.kernel.org/?p=platform/frameworks/base.git;a=blob;f=core/java/android/widget/TextView.java
public abstract class AbstractTextPosterElement extends AbstractPosterElement {

	protected String text = "";
	protected int foreColor;
	protected Typeface typeface = null;

	public AbstractTextPosterElement(int defaultForeColor, Typeface defaultTypeface) {
		this.foreColor = defaultForeColor;
		this.typeface = defaultTypeface;
	}

	public void setTypeface(Typeface typeface) {
		this.typeface = typeface;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setForeColor(int color) {
		this.foreColor = color;
	}

	@Override
	protected void onDraw(Canvas canvas, Rect region) {
		final int numLines = getNumLines();
		TextPainter tp = new TextPainter();
		tp.setFont(typeface);
		tp.setNumLines(numLines);
		tp.setTextColor(foreColor);
		tp.drawText(canvas, region, text);
	}

	protected TextPaint createPaintForText() {
		final TextPaint p = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		p.setAntiAlias(true);
		p.setColor(foreColor);
		p.setTypeface(typeface);
		p.setStrokeWidth(1);
		int flags = p.getFlags();
		flags = flags & ~Paint.DEV_KERN_TEXT_FLAG;
		p.setFlags(flags);
		return p;
	}
	
	protected abstract int getNumLines();
}
