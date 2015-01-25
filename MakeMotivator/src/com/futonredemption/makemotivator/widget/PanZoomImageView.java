package com.futonredemption.makemotivator.widget;

import org.beryl.graphics.BitmapWrapper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;

import com.futonredemption.makemotivator.util.HardwareUtils;
import com.futonredemption.makemotivator.util.RectUtils;
import com.sonyericsson.zoom.DynamicZoomControl;
import com.sonyericsson.zoom.ImageZoomView;
import com.sonyericsson.zoom.LongPressZoomListener;
import com.sonyericsson.zoom.PanZoomListener;
import com.sonyericsson.zoom.PinchZoomListener;
import com.sonyericsson.zoom.ZoomState;

public class PanZoomImageView extends ImageZoomView {

	private final PanZoomListener zoomListener;
	private final DynamicZoomControl zoomController = new DynamicZoomControl();

	Bitmap largeBitmap = null;

	public PanZoomImageView(Context context) {
        this(context, null, 0);
    }

	public PanZoomImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
    }

    public PanZoomImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		if(! this.isInEditMode()) {
			if(HardwareUtils.supportsMultiTouch(context)) {
				zoomListener = new PinchZoomListener(context);
			} else {
				zoomListener = new LongPressZoomListener(context);
			}
			zoomListener.setZoomControl(zoomController);
			final ZoomState zState = zoomController.getZoomState();
			this.setZoomState(zState);
			this.setOnTouchListener(zoomListener);
			zoomController.setAspectQuotient(getAspectQuotient());
		} else {
			// For edit mode.
			zoomListener = null;
		}
	}

    @Override
    public void setImageBitmap(Bitmap bitmap) {
    	largeBitmap = bitmap;
    	scaleBitmap();
    }

    public void setAndScaleImageBitmap(Bitmap bitmap) {
    	largeBitmap = bitmap;
    	scaleBitmap(this.getWidth(), this.getHeight(), true);
    }

    public Bitmap getBitmap() {
    	return largeBitmap;
    }

    public ZoomState getZoomState() {
    	return zoomController.getZoomState();
    }

	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	super.onSizeChanged(w, h, oldw, oldh);

    	if(! this.isInEditMode()) {
    		scaleBitmap(w, h, false);
    	}
    }

	private void scaleBitmap() {
		scaleBitmap(this.getWidth(), this.getHeight(), false);
	}

    private void scaleBitmap(int w, int h, boolean applyZoom) {

		if(w > 0 && h > 0 && largeBitmap != null) {
			final Rect viewRect = RectUtils.fromView(this);
			final Rect imageRect = RectUtils.fromBitmap(largeBitmap);
			final Rect scaleCenterCropRect = RectUtils.scaleCenterCrop(imageRect, viewRect);
			Bitmap scaledBitmap;
			final int viewWidth = viewRect.width();
			final int viewHeight = viewRect.height();
			int clampSize;

			// Make sure that the scale image is never larger than 512x512.

			if(imageRect.width() / (float)viewWidth > imageRect.height() / (float)viewHeight) {
				clampSize = viewWidth;
			} else {
				clampSize = viewHeight;
			}

			RectUtils.scaleRectToClampSize(scaleCenterCropRect, clampSize);

			scaledBitmap = Bitmap.createScaledBitmap(largeBitmap, scaleCenterCropRect.width(),
					scaleCenterCropRect.height(), true);

			final Bitmap deleteTarget = mBitmap;
			super.setImageBitmap(scaledBitmap);

			BitmapWrapper.dispose(deleteTarget);

			final float zoomWidth1 = (float)viewRect.width() / scaleCenterCropRect.width();
			final float zoomWidth2 = (float)scaleCenterCropRect.width() / viewRect.width();
			final float zoomHeight1 = (float)viewRect.height() / scaleCenterCropRect.height();
			final float zoomHeight2 = (float)scaleCenterCropRect.height() / viewRect.height();

			final float zoom = Math.max(
					Math.max(zoomWidth1, zoomWidth2),
					Math.max(zoomHeight1, zoomHeight2)
					);

			// The zoom must be at least the minimum default zoom.
			final ZoomState zState = zoomController.getZoomState();

			if(zoom > zState.getZoom()) {
				zState.setZoom(zoom);
			}

			if(applyZoom) {
				centerZoom(zoom);
			} else {
				resyncZoomState();
			}
		}
	}

	public void resyncZoomState() {
    	final ZoomState zState = zoomController.getZoomState();
    	zState.invalidate();
    }
    public void centerZoom(float zoom) {
    	final ZoomState zState = zoomController.getZoomState();
    	zState.setZoom(zoom);
    	zState.setPanX(0.5F);
    	zState.setPanY(0.5F);
    	zState.invalidate();
	}

    @Override
    public Parcelable onSaveInstanceState() {
    	Parcelable superState = super.onSaveInstanceState();
    	SavedState ss = new SavedState(superState);
    	ss.largeBitmap = this.largeBitmap;

    	return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
    	if(!(state instanceof SavedState)) {
    		super.onRestoreInstanceState(state);
    	} else {
    		SavedState ss = (SavedState)state;
    		super.onRestoreInstanceState(ss.getSuperState());

    		this.largeBitmap = ss.largeBitmap;
    		scaleBitmap();
    	}
    }

	public static class SavedState extends BaseSavedState {

		Bitmap largeBitmap;

		SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);

			largeBitmap = in.readParcelable(null);
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeParcelable(largeBitmap, flags);
		}

		@Override
		public String toString() {
			return super.toString();
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}
}
