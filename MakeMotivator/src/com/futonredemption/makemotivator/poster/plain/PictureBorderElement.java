package com.futonredemption.makemotivator.poster.plain;

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
			return new Rect(70, 45, 680, 455);
		} else {
			return new Rect(50, 50, 550, 600);
		}
	}
}
