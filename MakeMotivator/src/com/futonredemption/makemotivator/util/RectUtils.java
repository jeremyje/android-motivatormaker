package com.futonredemption.makemotivator.util;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

public class RectUtils {

	public static Rect fromBitmap(Bitmap bitmap) {
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		return rect;
	}

	public static Rect fromView(View view) {
		final Rect rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
		return rect;
	}

	public static Rect scaleCenterCrop(Rect src, Rect dest) {
		float scaleWidthRatio = (float)src.width() / dest.width();
		float scaleHeightRatio = (float)src.height() / dest.height();
		int newWidth;
		int newHeight;

		if(scaleWidthRatio < scaleHeightRatio) {
			newWidth = dest.width();
			newHeight = (int)(src.height() * (1.0F / scaleWidthRatio));
			float scaleHeight = (float)newHeight / dest.height();
			if(scaleHeight < 1.0F) {
				newWidth *= scaleHeight;
				newHeight *= scaleHeight;
			}
		} else {
			newHeight = dest.height();
			newWidth = (int)(src.width() * (1.0F / scaleHeightRatio));

			float scaleWidth = (float)newWidth / dest.width();
			if(scaleWidth < 1.0F) {
				newWidth *= scaleWidth;
				newHeight *= scaleWidth;
			}
		}

		final Rect rect = new Rect(0, 0, newWidth, newHeight);
		return rect;
	}

	public static Rect scale(RectF src, RectF parent, Scale scaleFactor) {
		return scale(src, scaleFactor.scaleX, scaleFactor.scaleY, parent);
	}


	public static Rect scale(RectF src, float scaleX, float scaleY, RectF parent) {
		int parentX = 0;
		int parentY = 0;

		if(parent != null) {
			parentX = (int) parent.left;
			parentY = (int) parent.top;
		}

		int newX = (int) ((src.left - parentX) * scaleX) + parentX;
		int newY = (int) ((src.top - parentY) * scaleY) + parentY;
		int newWidth = (int) (src.width() * scaleX);
		int newHeight = (int) (src.height() * scaleY);
		return new Rect(newX, newY, newX + newWidth, newY + newHeight);
	}


	public static float calcAspectQuotient(float viewWidth, float viewHeight, float contentWidth, float contentHeight) {
        return contentWidth / contentHeight / (viewWidth / viewHeight);
	}

	public static void calcBitmapClipRect(Rect inOutSrc, Rect inOutDest, float panX, float panY, float scale) {
        final int destWidth = inOutDest.width();
        final int destHeight = inOutDest.height();
        final int srcWidth = inOutSrc.width();
        final int srcHeight = inOutSrc.height();

        final float aspectQuotient = calcAspectQuotient(destWidth, destHeight, srcWidth, srcHeight);

        final float zoomX = Math.min(scale, scale * aspectQuotient) * destWidth / srcWidth;
        final float zoomY = Math.min(scale, scale / aspectQuotient) * destHeight / srcHeight;

        // Setup source and destination rectangles
        inOutSrc.left = (int)(panX * srcWidth - destWidth / (zoomX * 2));
        inOutSrc.top = (int)(panY * srcHeight - destHeight / (zoomY * 2));
        inOutSrc.right = (int)(inOutSrc.left + destWidth / zoomX);
        inOutSrc.bottom = (int)(inOutSrc.top + destHeight / zoomY);

        // Adjust source rectangle so that it fits within the source image.
        if (inOutSrc.left < 0) {
        	inOutDest.left += -inOutSrc.left * zoomX;
        	inOutSrc.left = 0;
        }
        if (inOutSrc.right > srcWidth) {
        	inOutDest.right -= (inOutSrc.right - srcWidth) * zoomX;
        	inOutSrc.right = srcWidth;
        }
        if (inOutSrc.top < 0) {
        	inOutDest.top += -inOutSrc.top * zoomY;
        	inOutSrc.top = 0;
        }
        if (inOutSrc.bottom > srcHeight) {
        	inOutDest.bottom -= (inOutSrc.bottom - srcHeight) * zoomY;
        	inOutSrc.bottom = srcHeight;
        }
    }

	public static void scaleRectToClampSize(Rect rect, int sizeClamp) {
		final int origWidth = rect.width();
		final int origHeight = rect.height();

		if(origWidth > origHeight) {
			if(origWidth > sizeClamp) {
				final float ratio = (float) origHeight / origWidth;
				rect.right = rect.left + sizeClamp;
				rect.bottom = rect.top + (int)(sizeClamp * ratio);
			}
		} else if(origWidth > origHeight) {
			if(origHeight > sizeClamp) {
				final float ratio = (float) origWidth / origHeight;
				rect.bottom = rect.top + sizeClamp;
				rect.right = rect.left + (int)(sizeClamp * ratio);
			}
		}
	}
}
