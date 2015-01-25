package com.futonredemption.makemotivator.poster.measure;

import com.futonredemption.makemotivator.poster.AbstractPosterElement;

public class NormalPosterMeasurer extends AbstractPosterMeasurer {

	public String getName() {
		return "Normal";
	}

	public void measurePoster(AbstractPosterElement posterRoot) {
		MeasureParams params = new MeasureParams(1.0F, orientation);
		posterRoot.measure(params);
	}
}
