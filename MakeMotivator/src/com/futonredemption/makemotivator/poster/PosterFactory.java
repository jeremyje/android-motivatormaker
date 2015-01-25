package com.futonredemption.makemotivator.poster;

public class PosterFactory {

	final IPosterElementFactory elementFactory;

	public PosterFactory(IPosterElementFactory elementFactory) {
		this.elementFactory = elementFactory;
	}

	public AbstractPosterElement create(IPosterComposition composition) {

		AbstractPosterElement root = elementFactory.createRootElement();
		AbstractSolidColorPosterElement border = elementFactory.createPictureBorderElement();
		AbstractSolidColorPosterElement padding = elementFactory.createPicturePaddingElement();
		AbstractBitmapPosterElement graphic = elementFactory.createPictureGraphicElement();
		AbstractTextPosterElement title = elementFactory.createTitleElement();
		AbstractTextPosterElement subtitle = elementFactory.createSubtitleElement();

		border.setForeColor(composition.getBorderColor());
		title.setForeColor(composition.getTitleColor());
		title.setText(composition.getTitle());
		subtitle.setText(composition.getSubtitle());
		subtitle.setForeColor(composition.getSubtitleColor());

		graphic.setImagePan(composition.getImageBitmapPanX(), composition.getImageBitmapPanY());
		graphic.setImageScale(composition.getImageBitmapScale());
		graphic.setImageBitmap(composition.getImageBitmap());

		border.addChild(padding);
		padding.addChild(graphic);
		root.addChild(border);
		root.addChild(title);
		root.addChild(subtitle);

		return root;
	}
}
