package com.futonredemption.makemotivator.poster;

import android.graphics.Canvas;
import android.graphics.Rect;

public abstract class AbstractSolidColorPosterElement extends AbstractPosterElement {

	protected int solidColor;

	public AbstractSolidColorPosterElement(int defaultColor) {
		this.solidColor = defaultColor;
	}

	public void setForeColor(int color) {
		this.solidColor = color;
	}

	@Override
	protected void onDraw(Canvas canvas, Rect region) {
		clipCanvas(canvas, region);
		canvas.drawColor(solidColor);
		unclipCanvas(canvas);
	}
}
