package com.futonredemption.makemotivator.poster.fancy;

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
			return new Rect(75, 30, 435, 272);
		} else {
			return new Rect(51, 21, 272, 339);
		}
	}
}
