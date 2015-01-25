package com.futonredemption.makemotivator.fragments;

import android.os.Parcel;
import android.os.Parcelable;

public class CustomizerState implements Parcelable {

	public int originalForecolor;
	public int changedForecolor;

	public CustomizerState() {

	}
	public CustomizerState(Parcel in) {
		readFromParcel(in);
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
    	dest.writeInt(originalForecolor);
    	dest.writeInt(changedForecolor);
    }

    public void readFromParcel(Parcel in) {
    	originalForecolor = in.readInt();
    	changedForecolor = in.readInt();
    }

    public static final Parcelable.Creator<CustomizerState> CREATOR = new Parcelable.Creator<CustomizerState>() {
        public CustomizerState createFromParcel(Parcel in) {
            return new CustomizerState(in);
        }

        public CustomizerState[] newArray(int size) {
            return new CustomizerState[size];
        }
    };
}
