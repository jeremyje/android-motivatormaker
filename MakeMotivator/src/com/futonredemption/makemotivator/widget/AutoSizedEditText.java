package com.futonredemption.makemotivator.widget;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.EditText;

import com.futonredemption.makemotivator.util.FontUtils;

public class AutoSizedEditText extends EditText {

	public AutoSizedEditText(Context context) {
		super(context);
	}

	public AutoSizedEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AutoSizedEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onTextChanged(CharSequence text, int start, int before, int after) {
		super.onTextChanged(text, start, before, after);
		adjustFontSize();
	}

	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	super.onSizeChanged(w, h, oldw, oldh);
    	adjustFontSize();
    }

	private void adjustFontSize() {
		if(this.getHeight() > 0 && this.getWidth() > 0) {
			String text = this.getText().toString();
			final TextPaint paint = this.getPaint();
			Rect rect = new Rect();
			getDrawingRect(rect);
			rect.bottom -= this.getPaddingBottom();
			rect.top += this.getPaddingTop();
			rect.left += this.getPaddingLeft();
			rect.right -= this.getPaddingRight();
			final float size = FontUtils.getMaxTextSize(text, paint, rect);
			setTextSize(size);
			int scrollX = getScrollX();
	    	int scrollY = this.getHeight();
	    	this.scrollTo(scrollX, scrollY);
		}
	}

	@Override
	public void setTextSize(float size) {
		if(size != this.getTextSize()) {
			super.setTextSize(size);
		}
	}
}
