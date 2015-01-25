package com.futonredemption.makemotivator.poster.measure;

import com.futonredemption.makemotivator.poster.AbstractPosterElement;

public interface IPosterMeasurer {
	String getName();
	void setOrientation(int orientation);
	void measurePoster(AbstractPosterElement posterRoot);
}
