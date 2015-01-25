package com.futonredemption.makemotivator.poster.fancy;

import android.graphics.Color;
import android.graphics.Rect;

import com.futonredemption.makemotivator.poster.AbstractSolidColorPosterElement;
import com.futonredemption.makemotivator.poster.measure.MeasureParams;

public class PosterRootElement extends AbstractSolidColorPosterElement {

	public PosterRootElement() {
		super(Color.BLACK);
	}

	@Override
	public Rect getBaseMeasurements(MeasureParams params) {
		if(params.orientation == MeasureParams.ORIENTATION_Landscape) {
			return new Rect(0, 0, 507, 362);
		} else {
			return new Rect(0, 0, 320, 429);
		}
	}
}
