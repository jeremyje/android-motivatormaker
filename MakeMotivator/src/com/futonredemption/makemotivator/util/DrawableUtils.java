package com.futonredemption.makemotivator.util;

import java.lang.reflect.Field;

import org.beryl.app.AndroidVersion;
import org.beryl.diagnostics.ExceptionReporter;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;

public class DrawableUtils {

	static interface IDrawableUtils {
		void setColor(ColorDrawable drawable, int color);
		int getBackgroundColor(View v);
	}
	
	private static final IDrawableUtils drawableUtils;
	
	static {
		if(AndroidVersion.isHoneycombOrHigher()) {
			drawableUtils = new HoneycombDrawableUtils();
		} else {
			drawableUtils = new BaseDrawableUtils();
		}
	}
	
	public static void setColor(ColorDrawable drawable, int color) {
		drawableUtils.setColor(drawable, color);
	}
	
	public static int getBackgroundColor(View v) {
		return drawableUtils.getBackgroundColor(v);
	}
	
	public static int dampenColor(final int color) {
		return Color.argb(0x99, Color.red(color), Color.green(color), Color.blue(color));
	}
	
	public static void setCustomizerDrawableColor(final View target, final Drawable drawable, final int color) {
		final LayerDrawable layer = (LayerDrawable)drawable;
		final GradientDrawable gradient = (GradientDrawable)layer.getDrawable(0);
		gradient.setColor(color);
		gradient.invalidateSelf();
		layer.invalidateSelf();
		target.invalidate();
	}
	
	// Compatibility Methods
	
	private static class BaseDrawableUtils implements IDrawableUtils {
		public void setColor(final ColorDrawable drawable, final int color) {
			try {
				Field mState = ColorDrawable.class.getDeclaredField("mState");
				mState.setAccessible(true);
				Class<?>mStateClass = mState.getType();
				Field mState_mUseColor = mStateClass.getDeclaredField("mUseColor");
				mState_mUseColor.setAccessible(true);
				mState_mUseColor.setInt(mState.get(drawable), color);
			} catch (Exception e) {
				ExceptionReporter.report(e);
			}
		}

		public int getBackgroundColor(final View v) {
			final ColorDrawable color = (ColorDrawable)v.getBackground();
			int colorValue = Color.WHITE;

			try {
				Field mState = ColorDrawable.class.getDeclaredField("mState");
				mState.setAccessible(true);
				Class<?>mStateClass = mState.getType();
				Field mState_mUseColor = mStateClass.getDeclaredField("mUseColor");
				mState_mUseColor.setAccessible(true);
				colorValue = mState_mUseColor.getInt(mState.get(color));
			} catch (Exception e) {
				ExceptionReporter.report(e);
			}

			return colorValue;
		}
		
	}
	
	@TargetApi(11)
	private static class HoneycombDrawableUtils implements IDrawableUtils {
		public void setColor(final ColorDrawable drawable, final int color) {
			drawable.setColor(color);
		}

		public int getBackgroundColor(final View v) {
			final ColorDrawable color = (ColorDrawable)v.getBackground();
			return color.getColor();
		}
		
	}
}
