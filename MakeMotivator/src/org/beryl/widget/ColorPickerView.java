package org.beryl.widget;

import java.util.ArrayList;

import com.futonredemption.makemotivator.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/** Allows users to select a color by sliding their finger on an HSV color hue gradient. */
public class ColorPickerView extends View {

	public static interface OnColorChangedListener {
		void onColorChanged(int color);
		void onPreviewColorChanged(int color);
	}

	private final Paint selectedColor = new Paint(Paint.ANTI_ALIAS_FLAG);
	private OnColorChangedListener onColorChangedListener = null;
	private int currentSelectionX = -1;
	
	private SolidColorSelector solidSelector = new SolidColorSelector();
	private GradientColorSelector gradientSelector = new GradientColorSelector();

	public ColorPickerView(final Context context) {
    	this(context, null, 0);

	}
	public ColorPickerView(final Context context, final AttributeSet attrs) {
		this(context, attrs,0);
	}

	public ColorPickerView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		setupAttributes(attrs);
		selectedColor.setStyle(Paint.Style.STROKE);
	}

	private void setupAttributes(AttributeSet attrs) {
		if(attrs != null) {
			final Context context = getContext();

			final TypedArray attribs = context.obtainStyledAttributes(attrs, R.styleable.ColorPickerView);
			String colorPaletteCsvString = attribs.getString(R.styleable.ColorPickerView_ColorPalette);
			setColorPalette(colorPaletteCsvString);
			attribs.recycle();
		}
	}
	
	public final void setColorPalette(final String colorPaletteCsvString) {
		clearColors();
		if(colorPaletteCsvString != null) {
			String [] colors = colorPaletteCsvString.split(",");
			for(String colorString : colors) {
				final int color = Color.parseColor(colorString);
				addColor(color);
			}
		}
	}
	
	public static int getCursorSize() {
		return 10;
	}
	
	public void setOnColorChangedListener(OnColorChangedListener listener) {
		this.onColorChangedListener = listener;
	}
	
	public void addColor(int color) {
		solidSelector.addColor(color);
	}
	
	public void clearColors() {
		solidSelector.clearColors();
	}
	
	private static abstract class ColorSelector {
		abstract int measureSize(int width, int height);
		protected abstract int onHitColor(float x, float y);
		
		protected final RectF region = new RectF();
		
		public boolean hitTest(float x, float y) {
			return region.contains(x, y);
		}
		
		protected float getInnerX(float x) {
			return x - region.left;
		}
		
		protected float getInnerY(float y) {
			return y - region.top;
		}
		
		public void setRegion(float left, float top, float right, float bottom) {
			this.region.left = left;
			this.region.top = top;
			this.region.right = right;
			this.region.bottom = bottom;
			onRegionChanged();
		}
		
		public abstract void onRegionChanged();
		public abstract void onDraw(Canvas canvas);
		
		public int hitColor(float x, float y) {
			return onHitColor(getInnerX(x), getInnerY(y));
		}
	}
	
	private static class SolidColorSelector extends ColorSelector {
		private final ArrayList<Integer> solidColors = new ArrayList<Integer>();
		private final Paint colorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

		public void clearColors() {
			solidColors.clear();
		}
		
		public void addColor(int color) {
			solidColors.add(Integer.valueOf(color));
		}
		
		@Override
		public int measureSize(int width, int height) {
			final int cellSize = Math.min(Math.min(width, height), getCursorSize());
			final int totalSize = solidColors.size() * cellSize;
			return totalSize;
		}

		@Override
		public void onDraw(Canvas canvas) {
			final int numColors = solidColors.size();
			if(numColors > 0) {
				final float cellSize = region.width() / numColors;
				final float x = region.left;
				
				for(int i = 0; i < numColors; i++) {
					final int color = solidColors.get(i).intValue();
					colorPaint.setColor(color);
					canvas.drawRect(x + (cellSize * i), region.top, x + (cellSize * (i + 1)), region.bottom, colorPaint);
				}
			}
		}

		@Override
		protected int onHitColor(float x, float y) {
			final float size = region.width() / solidColors.size();
			final int index = (int)(x / size);
			return solidColors.get(index);
		}

		@Override
		public void onRegionChanged() {
		}
	}
	
	private static class GradientColorSelector extends ColorSelector {

		private static final int [] colorSet = new int[] {
	        0xFFFF0000, 0xFFFFFF00, 0xFF00FF00,
	        0xFF00FFFF, 0xFF0000FF, 0xFFFF00FF,
	        0xFFFF0000
	    };

		private final Paint colorRange = new Paint(Paint.ANTI_ALIAS_FLAG);
		private LinearGradient shader;
		
		GradientColorSelector() {
			colorRange.setStyle(Paint.Style.STROKE);
			shader = new LinearGradient(0.0F, 0.0F, 0.0F, 0.0F, colorSet, null, Shader.TileMode.CLAMP);
		}
		
		public int measureSize(int width, int height) {
			return width;
		}

		public void onDraw(Canvas canvas) {
			colorRange.setShader(shader);
			colorRange.setStrokeWidth(region.height());
			
			canvas.drawLine(region.left, region.top, region.width(), region.top, colorRange);
		}

		private float convertToDegrees(float x) {
			return 360.0f * x / region.width();
		}

		@Override
		protected int onHitColor(float x, float y) {
			float deg = convertToDegrees(x);
			final int color = Color.HSVToColor(new float [] { deg, 1.0f, 1.0f });
			return color;
		}

		@Override
		public void onRegionChanged() {
			shader = new LinearGradient(region.left, region.top, region.right, region.bottom, colorSet, null, Shader.TileMode.CLAMP);
		}
	}

	@Override
    protected void onDraw(Canvas canvas) {
		final int width = this.getWidth();
		final int height = this.getHeight();
		//final int drawHeight = (int) (height / 2);
		final int centerY = (int) (height / 2);
		
		final int solidSize = solidSelector.measureSize(width, height);
		//final int gradientSize = gradientSelector.measureSize(width - solidSize, height);
		
		solidSelector.setRegion(width - solidSize, centerY - (centerY / 2), width, centerY + (centerY / 2));
		
		gradientSelector.setRegion(0.0F, centerY, width - solidSize, height);
		
		gradientSelector.onDraw(canvas);
		solidSelector.onDraw(canvas);

		if(currentSelectionX != -1) {
			int color = determineColor(currentSelectionX);
			final int strokeWidth = Math.min((int) (width / 360.0f * 10.0f), getCursorSize());
			selectedColor.setStrokeWidth(strokeWidth);
			selectedColor.setColor(color);
			canvas.drawLine(currentSelectionX, 0, currentSelectionX, height, selectedColor);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean handled = false;
		final int action = event.getAction();
		final float x = event.getX();
		//final float y = event.getY();

		switch(action) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
			{
				final int color = determineColor(x);
				if(onColorChangedListener != null) {
					onColorChangedListener.onPreviewColorChanged(color);
				}
				currentSelectionX = (int)x;
				handled = true;
			} break;
			case MotionEvent.ACTION_UP:
			{
				final int color = determineColor(x);
				if(onColorChangedListener != null) {
					onColorChangedListener.onColorChanged(color);
				}
				currentSelectionX = -1;
				handled = true;
			} break;
		}

		if(handled) {
			// FIXME: Only invalidate a section.
			this.invalidate();
		}
		return handled;
	}

	private int determineColor(float x) {
		final int y = getHeight() / 2;
		int color = Color.WHITE;
		if(solidSelector.hitTest(x, y)) {
			color = solidSelector.hitColor(x, y);
		} else if (gradientSelector.hitTest(x, y)) {
			color = gradientSelector.hitColor(x, y);
		}
		
		return color;
	}
}
