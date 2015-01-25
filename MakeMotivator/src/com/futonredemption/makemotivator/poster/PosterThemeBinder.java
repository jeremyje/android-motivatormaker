package com.futonredemption.makemotivator.poster;

import java.util.ArrayList;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;

import com.futonredemption.makemotivator.poster.measure.MeasureParams;
import com.futonredemption.makemotivator.widget.ScaledCompositeLayout;

public class PosterThemeBinder {

	final ArrayList<UiElement> elements = new ArrayList<UiElement>();
	UiElement baseElement = null;

	static class UiElement {
		final View view;
		final AbstractPosterElement element;
		final ScaledCompositeLayout.LayoutParams layoutParams;

		UiElement(View view, AbstractPosterElement element) {
			this.view = view;
			this.element = element;

			ViewGroup.LayoutParams baseParams = view.getLayoutParams();
			if(baseParams instanceof ScaledCompositeLayout.LayoutParams) {
				layoutParams = (ScaledCompositeLayout.LayoutParams)baseParams;
			} else {
				layoutParams = null;
			}
		}

		public Rect getVirtualMeasurements(MeasureParams params) {
			return element.getBaseMeasurements(params);
		}

		public boolean isLayoutValid() {
			return layoutParams != null;
		}
		public RectF getVirtualRect() {
			return layoutParams.virtualRect;
		}
	}

	public void setBase(View view, AbstractPosterElement element) {
		baseElement = new UiElement(view, element);
	}

	public void bind(View view, AbstractPosterElement element) {
		UiElement newElement = new UiElement(view, element);
		elements.add(newElement);
	}

	public void resizeLayout(boolean resizeArea, MeasureParams params) {

		int left = -1;
		int right = -1;
		int bottom = -1;
		int top = -1;

		ScaledCompositeLayout layout = (ScaledCompositeLayout)baseElement.view;

		if(resizeArea) {
			for(UiElement element : elements) {
				if(element.isLayoutValid()) {
					Rect rect = element.getVirtualMeasurements(params);
					left = min(rect.left, left);
					right = max(rect.right, right);
					bottom = max(rect.bottom, bottom);
					top = min(rect.top, top);
				}
			}
		} else {
			Rect baseRect = baseElement.getVirtualMeasurements(params);
			left = baseRect.left;
			top = baseRect.top;
			right = baseRect.right;
			bottom = baseRect.bottom;
		}

		final int width = right - left;
		final int height = bottom - top;

		layout.virtualWidth = width;
		layout.virtualHeight = height;
		layout.invalidate();

		for(UiElement element : elements) {
			if(element.isLayoutValid()) {
				ScaledCompositeLayout.LayoutParams elementLayoutParams = element.layoutParams;

				Rect virtMeasure = element.getVirtualMeasurements(params);
				int elementWidth = virtMeasure.width();
				int elementHeight = virtMeasure.height();
				RectF elementVirtual = elementLayoutParams.virtualRect;
				elementVirtual.left = virtMeasure.left - left;
				elementVirtual.top = virtMeasure.top - top;
				elementVirtual.right = virtMeasure.left - left + elementWidth;
				elementVirtual.bottom = virtMeasure.top - top + elementHeight;
			}
		}
	}

	static int max(int candidate, int original) {
		if(original == -1.0F || original < candidate)
			return candidate;
		else
			return original;
	}

	static int min(int candidate, int original) {
		if(original == -1.0F || original > candidate)
			return candidate;
		else
			return original;
	}
}
