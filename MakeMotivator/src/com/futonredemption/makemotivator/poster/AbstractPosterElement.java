package com.futonredemption.makemotivator.poster;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region.Op;

import com.futonredemption.makemotivator.poster.measure.MeasureParams;
import com.futonredemption.makemotivator.util.ColorUtils;

public abstract class AbstractPosterElement {
	private final ArrayList<AbstractPosterElement> children = new ArrayList<AbstractPosterElement>();
	protected Rect drawingRegion = null;
	protected MeasureParams measureParams = null;

	static final boolean DEBUG_DRAW = false;

	public abstract Rect getBaseMeasurements(MeasureParams params);

	protected abstract void onDraw(Canvas canvas, Rect region);
	public void draw(Canvas canvas) {

		final Rect region = getDrawingRegion();

		if(DEBUG_DRAW) {
			debugDraw(canvas, region);
		}

		onDraw(canvas, region);
		for(AbstractPosterElement child : children) {
			child.draw(canvas);
		}
	}

	protected void debugDraw(Canvas canvas, Rect region) {
		clipCanvas(canvas, region);
		canvas.drawColor(ColorUtils.randomColor());
		unclipCanvas(canvas);
	}

	public void measure(MeasureParams params) {
		final Rect baseRegion = getBaseMeasurements(params);

		measureParams = params;
		onMeasure(baseRegion, params);

		for(AbstractPosterElement child : children) {
			child.measure(params);
		}
	}

	public int getWidth() {
		return drawingRegion.width();
	}

	public int getHeight() {
		return drawingRegion.height();
	}

	protected void onMeasure(Rect baseRegion, MeasureParams params) {
		drawingRegion = new Rect(baseRegion);
		final float width = drawingRegion.width();
		final float height = drawingRegion.height();
		drawingRegion.left *= params.getScaleX();
		drawingRegion.top *= params.getScaleY();
		drawingRegion.right = (int) (drawingRegion.left + width * params.getScaleX());
		drawingRegion.bottom = (int) (drawingRegion.top + height * params.getScaleY());
	}

	protected Rect getDrawingRegion() {
		return drawingRegion;
	}

	protected void clipCanvas(Canvas canvas, Rect region) {
		canvas.clipRect(region, Op.REPLACE);
	}

	protected void unclipCanvas(Canvas canvas) {
		canvas.clipRect(0, 0, canvas.getWidth(), canvas.getHeight(), Op.REPLACE);
	}

	public void addChild(AbstractPosterElement child) {
		children.add(child);
	}

	protected boolean isInDebugDrawMode() {
		return DEBUG_DRAW;
	}
}
