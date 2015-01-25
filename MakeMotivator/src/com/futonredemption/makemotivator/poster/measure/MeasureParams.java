package com.futonredemption.makemotivator.poster.measure;

import android.os.Parcel;
import android.os.Parcelable;

public class MeasureParams implements Parcelable {

	public static final int ORIENTATION_Portrait = 1;
	public static final int ORIENTATION_Landscape = 2;

	public float scale;
	public int orientation;

	public MeasureParams() {
		scale = 1.0f;
		orientation = ORIENTATION_Portrait;
	}

	public MeasureParams(Parcel in) {
		readFromParcel(in);
	}

	public MeasureParams(float scale, int orientation) {
		this.scale = scale;
		this.orientation = orientation;
	}

	public float getScaleX() {
		return scale;
	}

	public float getScaleY() {
		return scale;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
    	dest.writeFloat(scale);
    	dest.writeInt(orientation);
    }

    public void readFromParcel(Parcel in) {
    	scale = in.readFloat();
		orientation = in.readInt();
    }

    public static final Parcelable.Creator<MeasureParams> CREATOR = new Parcelable.Creator<MeasureParams>() {
        public MeasureParams createFromParcel(Parcel in) {
            return new MeasureParams(in);
        }

        public MeasureParams[] newArray(int size) {
            return new MeasureParams[size];
        }
    };
}
