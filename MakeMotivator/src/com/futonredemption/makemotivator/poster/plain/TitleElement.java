package com.futonredemption.makemotivator.poster.plain;

import android.graphics.Color;
import android.graphics.Rect;

import com.futonredemption.makemotivator.poster.AbstractTextPosterElement;
import com.futonredemption.makemotivator.poster.measure.MeasureParams;
import com.futonredemption.makemotivator.util.FontUtils;

public class TitleElement extends AbstractTextPosterElement {

	public TitleElement() {
		super(Color.WHITE, FontUtils.getCaslon540Bt(null));
	}

	@Override
	public Rect getBaseMeasurements(MeasureParams params) {
		if(params.orientation == MeasureParams.ORIENTATION_Landscape) {
			return new Rect(42, 462, 707, 512);
		} else {
			return new Rect(40, 610, 560, 662);
		}
	}

	@Override
	public void setText(String text) {
		this.text = text.toUpperCase();
	}
	
	@Override
	protected int getNumLines() {
		return 1;
	}
}
