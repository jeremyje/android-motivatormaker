package com.futonredemption.makemotivator.poster;

import android.graphics.Bitmap;

import com.futonredemption.makemotivator.poster.measure.MeasureParams;

public interface IPosterComposition {

	public String getTitle();
	public void setTitle(String title);

	public String getSubtitle();
	public void setSubtitle(String subtitle);

	public Bitmap getImageBitmap();
	public void setImageBitmap(Bitmap picture);

	public int getTitleColor();
	public void setTitleColor(int titleColor);

	public int getSubtitleColor();
	public void setSubtitleColor(int subtitleColor);

	public int getBorderColor();
	public void setBorderColor(int borderColor);

	public String getThemeTag();
	public void setThemeTag(String themeId);
	
	String getFileFormat();
	void setFileFormat(String fileFormat);
	
	float getSizeMultiplier();
	void setSizeMultiplier(float sizeMultiplier);

	public float getImageBitmapPanX();
	public void setImageBitmapPanX(float imageBitmapPanX);

	public float getImageBitmapPanY();
	public void setImageBitmapPanY(float imageBitmapPanY);

	public float getImageBitmapScale();
	public void setImageBitmapScale(float imageBitmapScale);

	public boolean getIsBlankImage();
	public void setIsBlankImage(boolean isBlank);
	
	public MeasureParams getMeasureParams();
	public void setMeasureParams(MeasureParams params);
}