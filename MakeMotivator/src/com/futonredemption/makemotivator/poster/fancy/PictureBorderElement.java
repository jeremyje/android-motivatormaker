package com.futonredemption.makemotivator.poster.fancy;

import android.graphics.Color;
import android.graphics.Rect;

import com.futonredemption.makemotivator.poster.AbstractSolidColorPosterElement;
import com.futonredemption.makemotivator.poster.measure.MeasureParams;

public class PictureBorderElement extends AbstractSolidColorPosterElement {

	public PictureBorderElement() {
		super(Color.WHITE);
	}

	@Override
	public Rect getBaseMeasurements(MeasureParams params) {
		if(params.orientation == MeasureParams.ORIENTATION_Landscape) {
			return new Rect(73, 28, 437, 274);
		} else {
			return new Rect(49, 19, 274, 341);
		}
	}
}
