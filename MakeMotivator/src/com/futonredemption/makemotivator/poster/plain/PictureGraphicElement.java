package com.futonredemption.makemotivator.poster.plain;

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
			return new Rect(74, 49, 676, 451);
		} else {
			return new Rect(54, 54, 546, 596);
		}
	}
}
