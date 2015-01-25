package com.futonredemption.makemotivator.poster.fancy;

import com.futonredemption.makemotivator.poster.AbstractBitmapPosterElement;
import com.futonredemption.makemotivator.poster.AbstractPosterElement;
import com.futonredemption.makemotivator.poster.AbstractSolidColorPosterElement;
import com.futonredemption.makemotivator.poster.AbstractTextPosterElement;
import com.futonredemption.makemotivator.poster.IPosterElementFactory;

public class FancyPosterElementFactory implements IPosterElementFactory {

	public FancyPosterElementFactory() {
	}

	public AbstractPosterElement createRootElement() {
		return new PosterRootElement();
	}

	public AbstractSolidColorPosterElement createPictureBorderElement() {
		return new PictureBorderElement();
	}

	public AbstractSolidColorPosterElement createPicturePaddingElement() {
		return new PicturePaddingElement();
	}

	public AbstractBitmapPosterElement createPictureGraphicElement() {
		return new PictureGraphicElement();
	}

	public AbstractTextPosterElement createTitleElement() {
		return new TitleElement();
	}
	public AbstractTextPosterElement createSubtitleElement() {
		return new SubtitleElement();
	}

	public String getDescription() {
		return "Close to the real looking motivators.";
	}

	public String getName() {
		return "Fancy";
	}
	
	public String getThemeTag() {
		return "fancy";
	}
}
