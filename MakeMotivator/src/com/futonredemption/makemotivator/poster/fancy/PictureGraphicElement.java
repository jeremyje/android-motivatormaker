package com.futonredemption.makemotivator.poster.fancy;

import android.graphics.Rect;

import com.futonredemption.makemotivator.poster.AbstractBitmapPosterElement;
import com.futonredemption.makemotivator.poster.measure.MeasureParams;

public class PictureGraphicElement extends AbstractBitmapPosterElement {

	public PictureGraphicElement() {
		super();
	}

	@Override
	public Rect getBaseMeasurements(MeasureParams params) {
		if(params.orientation == MeasureParams.ORIENTATION_Landscape) {
			return new Rect(77, 32, 433, 270);
		} else {
			return new Rect(53, 23, 270, 337);
		}
	}
}
