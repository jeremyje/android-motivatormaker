package com.futonredemption.makemotivator.poster.plain;

import android.graphics.Color;
import android.graphics.Rect;

import com.futonredemption.makemotivator.poster.AbstractSolidColorPosterElement;
import com.futonredemption.makemotivator.poster.measure.MeasureParams;

public class PicturePaddingElement extends AbstractSolidColorPosterElement {

	public PicturePaddingElement() {
		super(Color.BLACK);
	}

	@Override
	public Rect getBaseMeasurements(MeasureParams params) {
		if(params.orientation == MeasureParams.ORIENTATION_Landscape) {
			return new Rect(72, 47, 678, 453);
		} else {
			return new Rect(52, 52, 548, 598);
		}
	}
}
