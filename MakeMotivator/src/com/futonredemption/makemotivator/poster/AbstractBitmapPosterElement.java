package com.futonredemption.makemotivator.poster;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.futonredemption.makemotivator.util.RectUtils;

public abstract class AbstractBitmapPosterElement extends AbstractPosterElement {

	protected Bitmap picture = null;

	protected float panX = 0.5F;
	protected float panY = 0.5F;
	protected float scale = 1.0F;

	public void setImageBitmap(Bitmap bitmap) {
		this.picture = bitmap;
	}

	public void setImagePan(float panX, float panY) {
		this.panX = panX;
		this.panY = panY;
	}

	public void setImageScale(float scale) {
		this.scale = scale;
	}

	@Override
	protected void onDraw(Canvas canvas, Rect region) {

		if(picture != null) {
			final Paint p = createPaintForBitmap();
			final Rect srcRect = RectUtils.fromBitmap(picture);
			final Rect destRect = region;

			RectUtils.calcBitmapClipRect(srcRect, destRect, panX, panY, scale);

			canvas.drawBitmap(picture, srcRect, destRect, p);
		}
	}

	protected Paint createPaintForBitmap() {
		final Paint p = new Paint(Paint.FILTER_BITMAP_FLAG);

		return p;
	}
}
