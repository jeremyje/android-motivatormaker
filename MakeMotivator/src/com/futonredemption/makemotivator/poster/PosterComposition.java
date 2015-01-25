package com.futonredemption.makemotivator.poster;

import org.beryl.graphics.BitmapWrapper;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import com.futonredemption.makemotivator.Constants;
import com.futonredemption.makemotivator.poster.measure.MeasureParams;

public class PosterComposition implements Parcelable, IPosterComposition {

	private String title = "";
	private String subtitle = "";
	private Bitmap imageBitmap = null;
	private int titleColor = Color.WHITE;
	private int subtitleColor = Color.WHITE;
	private int borderColor = Color.WHITE;
	private String themeTag = Constants.Preferences.DefaultValue_PosterTheme;
	private float imageBitmapPanX = 0.5F;
	private float imageBitmapPanY = 0.5F;
	private float imageBitmapScale = 0.5F;
	private MeasureParams measure = new MeasureParams(1.0F, MeasureParams.ORIENTATION_Portrait);
	private String fileFormat = Constants.Preferences.DefaultValue_FileFormat;
	private float sizeMultiplier = 1.0F;
	private boolean isBlankImage = true;

	public PosterComposition() {

	}

	public PosterComposition(Parcel in) {
		readFromParcel(in);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public Bitmap getImageBitmap() {
		return imageBitmap;
	}

	public void setImageBitmap(Bitmap image) {
		if(this.imageBitmap != image) {
			BitmapWrapper.dispose(this.imageBitmap);
			this.imageBitmap = image;
		}
	}

	public int getTitleColor() {
		return titleColor;
	}

	public void setTitleColor(int titleColor) {
		this.titleColor = titleColor;
	}

	public int getSubtitleColor() {
		return subtitleColor;
	}

	public void setSubtitleColor(int subtitleColor) {
		this.subtitleColor = subtitleColor;
	}

	public int getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(int borderColor) {
		this.borderColor = borderColor;
	}

	public String getThemeTag() {
		return themeTag;
	}

	public void setThemeTag(String themeTag) {
		this.themeTag = themeTag;
	}

	public float getImageBitmapPanX() {
		return imageBitmapPanX;
	}

	public void setImageBitmapPanX(float imageBitmapPanX) {
		this.imageBitmapPanX = imageBitmapPanX;
	}

	public float getImageBitmapPanY() {
		return imageBitmapPanY;
	}

	public void setImageBitmapPanY(float imageBitmapPanY) {
		this.imageBitmapPanY = imageBitmapPanY;
	}

	public float getImageBitmapScale() {
		return imageBitmapScale;
	}

	public void setImageBitmapScale(float imageBitmapScale) {
		this.imageBitmapScale = imageBitmapScale;
	}

	public MeasureParams getMeasureParams() {
		return this.measure;
	}

	public void setMeasureParams(MeasureParams params) {
		this.measure = params;
	}
	
	public String getFileFormat() {
		return this.fileFormat;
	}

	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}
	
	public float getSizeMultiplier() {
		return this.sizeMultiplier;
	}

	public void setSizeMultiplier(float sizeMultiplier) {
		this.sizeMultiplier = sizeMultiplier;
	}
	

	public boolean getIsBlankImage() {
		return this.isBlankImage;
	}

	public void setIsBlankImage(boolean isBlank) {
		this.isBlankImage = isBlank;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
    	dest.writeString(title);
    	dest.writeString(subtitle);
    	dest.writeParcelable(imageBitmap, flags);
    	dest.writeInt(titleColor);
    	dest.writeInt(subtitleColor);
    	dest.writeInt(borderColor);
    	dest.writeFloat(imageBitmapPanX);
    	dest.writeFloat(imageBitmapPanY);
    	dest.writeFloat(imageBitmapScale);
    	dest.writeFloat(sizeMultiplier);
    	dest.writeString(themeTag);
    	dest.writeString(fileFormat);
    	dest.writeParcelable(measure, flags);
    	if(isBlankImage) {
    		dest.writeInt(1);
    	} else {
    		dest.writeInt(0);
    	}
    }

    public void readFromParcel(Parcel in) {
    	title = in.readString();
    	subtitle = in.readString();
		imageBitmap = in.readParcelable(null);
		titleColor = in.readInt();
		subtitleColor = in.readInt();
		borderColor = in.readInt();
		imageBitmapPanX = in.readFloat();
		imageBitmapPanY = in.readFloat();
		imageBitmapScale = in.readFloat();
		sizeMultiplier = in.readFloat();
		themeTag = in.readString();
		fileFormat = in.readString();
		measure = in.readParcelable(getClass().getClassLoader());
		isBlankImage = in.readInt() != 0;
    }

    public static final Parcelable.Creator<PosterComposition> CREATOR = new Parcelable.Creator<PosterComposition>() {
        public PosterComposition createFromParcel(Parcel in) {
            return new PosterComposition(in);
        }

        public PosterComposition[] newArray(int size) {
            return new PosterComposition[size];
        }
    };

    public static void copyTo(IPosterComposition src, IPosterComposition dest) {
		dest.setBorderColor(src.getBorderColor());
		dest.setImageBitmap(src.getImageBitmap());
		dest.setSubtitle(src.getSubtitle());
		dest.setSubtitleColor(src.getSubtitleColor());
		dest.setThemeTag(src.getThemeTag());
		dest.setTitle(src.getTitle());
		dest.setTitleColor(src.getTitleColor());
		dest.setImageBitmapPanX(src.getImageBitmapPanX());
		dest.setImageBitmapPanY(src.getImageBitmapPanY());
		dest.setImageBitmapScale(src.getImageBitmapScale());
		dest.setFileFormat(src.getFileFormat());
		dest.setMeasureParams(src.getMeasureParams());
		dest.setSizeMultiplier(src.getSizeMultiplier());
		dest.setIsBlankImage(src.getIsBlankImage());
	}

	public void destroy() {
		BitmapWrapper.dispose(this.imageBitmap);
		this.imageBitmap = null;
	}
}
