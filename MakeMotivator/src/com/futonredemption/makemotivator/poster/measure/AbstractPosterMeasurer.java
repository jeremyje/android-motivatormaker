package com.futonredemption.makemotivator.poster.measure;

public abstract class AbstractPosterMeasurer implements IPosterMeasurer {

	protected int orientation = MeasureParams.ORIENTATION_Landscape;

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}
}
