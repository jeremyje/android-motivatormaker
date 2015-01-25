package com.futonredemption.makemotivator.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;

import com.futonredemption.makemotivator.util.RectUtils;

public class ScaledCompositeLayout extends ViewGroup {

	@ViewDebug.ExportedProperty(category = "virtual-measurement")
	public float virtualHeight;

	@ViewDebug.ExportedProperty(category = "virtual-measurement")
	public float virtualWidth;

	@ViewDebug.ExportedProperty(category = "clamp-width")
	protected boolean clampOnWidth;


	public ScaledCompositeLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ScaledCompositeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);

		final TypedArray styleValues = context.obtainStyledAttributes(
						attrs,
						com.futonredemption.makemotivator.R.styleable.ScaledCompositeLayout,
						defStyle, 0);

		virtualWidth = styleValues.getDimension(
						com.futonredemption.makemotivator.R.styleable.ScaledCompositeLayout_VirtualWidth,
						-1);
		virtualHeight = styleValues.getDimension(
						com.futonredemption.makemotivator.R.styleable.ScaledCompositeLayout_VirtualHeight,
						-1);
		clampOnWidth = styleValues.getBoolean(
				com.futonredemption.makemotivator.R.styleable.ScaledCompositeLayout_ClampOnWidth,
				false);

		styleValues.recycle();

		if(virtualWidth == -1) {
			throw new IllegalStateException("virtualWidth is required parameter.");
		}
		if(virtualHeight == -1) {
			throw new IllegalStateException("virtualHeight is required parameter.");
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int numChildren = getChildCount();

		final Rect parentRect = new Rect(0, 0, this.getWidth(), this.getHeight());
		final float scaleX = this.getWidth() / virtualWidth;
		final float scaleY = this.getHeight() / virtualHeight;

		for(int i = 0; i < numChildren; i++) {
			final View child = getChildAt(i);
			final LayoutParams lp = (LayoutParams) child.getLayoutParams();
			final RectF lpr = lp.getRectF();

			final Rect dest = RectUtils.scale(lpr, scaleX, scaleY, new RectF(parentRect));
			child.layout(dest.left, dest.top, dest.right, dest.bottom);

			//Dimension out = dest;
			//throw new IllegalStateException(String.format("Position: %d x %d -- Size: %d x %d", out.x, out.y, out.width, out.height));
		}
	}

	/**
	 * Uses the Virtual Width and Height to determine the ratio size of the actual layout.
	 * The layout_width and layout_height parameters are used to determine the maximum bounds.
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int width, height;
		//int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		//int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		float maxWidth = MeasureSpec.getSize(widthMeasureSpec);
		float maxHeight = MeasureSpec.getSize(heightMeasureSpec);

		if (maxHeight == 0.0F) {
			clampOnWidth = true;
		}

		final float widthVirtualMultiplier = virtualWidth / virtualHeight;
		final float heightVirtualMultiplier = virtualHeight / virtualWidth;

		//final float widthActualMultiplier = maxWidth / maxHeight;
		//final float heightActualMultiplier = maxHeight / maxWidth;
		/*
		if(widthMode == MeasureSpec.EXACTLY) {
			width = (int)maxWidth;
			height = (int) (maxWidth * heightVirtualMultiplier);
		} else if(heightMode == MeasureSpec.EXACTLY) {
			width = (int)(maxHeight * widthVirtualMultiplier);
			height = (int)maxHeight;
		} else { // Use scaling */
			if(clampOnWidth || maxWidth * heightVirtualMultiplier < maxHeight) {
				width = (int)maxWidth;
				height = (int) (width * heightVirtualMultiplier);
			} else {
				height = (int)maxHeight;
				width = (int)(height * widthVirtualMultiplier);
			}
		//}

		setMeasuredDimension(width, height);

		final int numChildren = getChildCount();
		final float scaleX = width / virtualWidth;
		final float scaleY = height / virtualHeight;
		final Rect parentRect = new Rect(0, 0, this.getWidth(), this.getHeight());

		for(int i = 0; i < numChildren; i++) {
			final View child = getChildAt(i);
			final LayoutParams lp = (LayoutParams) child.getLayoutParams();
			final RectF lpr = lp.getRectF();

			final Rect dest = RectUtils.scale(lpr, scaleX, scaleY, new RectF(parentRect));

			this.measureChild(child, MeasureSpec.makeMeasureSpec(dest.width(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(dest.height(), MeasureSpec.EXACTLY));

			//Dimension out = dest;
			//throw new IllegalStateException(String.format("Position: %d x %d -- Size: %d x %d", out.x, out.y, out.width, out.height));
		}

		/*
		for(int i = 0; i < numChildren; i++) {
			final View child = getChildAt(i);
			measureChild(child, widthMeasureSpec, heightMeasureSpec);
		}
		*/

		//throw new IllegalStateException(String.format("%d x %d -- Physical: %.0f x %.0f -- Virtual: %.0f x %.0f", width, height, maxWidth, maxHeight, virtualWidth, virtualHeight));
	}

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new ScaledCompositeLayout.LayoutParams(getContext(), attrs);
	}

	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		return p instanceof LayoutParams;
	}

	@Override
	protected ViewGroup.LayoutParams generateLayoutParams(
			ViewGroup.LayoutParams p) {
		return new LayoutParams(p);
	}

	public static class LayoutParams extends
			android.view.ViewGroup.LayoutParams {

		public RectF virtualRect = new RectF();

		public RectF getRectF() {
			return virtualRect;
		}

		public LayoutParams(Context c, AttributeSet attrs) {
			super(c, attrs);
			float virtualWidth;
			float virtualHeight;

			TypedArray styleValues = c
					.obtainStyledAttributes(
							attrs,
							com.futonredemption.makemotivator.R.styleable.ScaledCompositeLayout_Layout);
			virtualRect.top = styleValues
					.getDimension(
							com.futonredemption.makemotivator.R.styleable.ScaledCompositeLayout_Layout_VirtualTop,
							0);
			virtualRect.left = styleValues
					.getDimension(
							com.futonredemption.makemotivator.R.styleable.ScaledCompositeLayout_Layout_VirtualLeft,
							0);
			virtualRect.right = styleValues
					.getDimension(
							com.futonredemption.makemotivator.R.styleable.ScaledCompositeLayout_Layout_VirtualRight,
							0);
			virtualRect.bottom = styleValues
					.getDimension(
							com.futonredemption.makemotivator.R.styleable.ScaledCompositeLayout_Layout_VirtualBottom,
							0);

			virtualWidth = styleValues
					.getDimension(
							com.futonredemption.makemotivator.R.styleable.ScaledCompositeLayout_Layout_VirtualWidth,
							-1);
			if (virtualWidth > 0) {
				virtualRect.right = virtualRect.left + virtualWidth;
			}

			virtualHeight = styleValues
					.getDimension(
							com.futonredemption.makemotivator.R.styleable.ScaledCompositeLayout_Layout_VirtualHeight,
							-1);
			if (virtualHeight > 0) {
				virtualRect.bottom = virtualRect.top + virtualHeight;
			}

			styleValues.recycle();
		}

		public LayoutParams(int width, int height) {
			super(width, height);
		}

		public LayoutParams(ViewGroup.LayoutParams source) {
			super(source);
		}
	}
}
