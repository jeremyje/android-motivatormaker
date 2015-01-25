package com.futonredemption.makemotivator.poster.fancy;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.futonredemption.makemotivator.poster.AbstractTextPosterElement;
import com.futonredemption.makemotivator.poster.measure.MeasureParams;

public class SubtitleElement extends AbstractTextPosterElement {

	public SubtitleElement() {
		super(Color.WHITE, Typeface.SANS_SERIF);
	}

	@Override
	public Rect getBaseMeasurements(MeasureParams params) {
		if(params.orientation == MeasureParams.ORIENTATION_Landscape) {
			return new Rect(50,322, 456, 349);
		} else {
			return new Rect(49, 390, 276, 416);
		}
	}

	@Override
	protected int getNumLines() {
		return 2;
	}
}
