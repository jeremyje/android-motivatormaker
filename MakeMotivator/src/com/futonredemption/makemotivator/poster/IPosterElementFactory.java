package com.futonredemption.makemotivator.poster;

public interface IPosterElementFactory {
	String getName();
	String getDescription();
	String getThemeTag();

	AbstractPosterElement createRootElement();
	AbstractSolidColorPosterElement createPictureBorderElement();
	AbstractSolidColorPosterElement createPicturePaddingElement();
	AbstractBitmapPosterElement createPictureGraphicElement();
	AbstractTextPosterElement createTitleElement();
	AbstractTextPosterElement createSubtitleElement();
}
