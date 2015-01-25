package com.futonredemption.makemotivator.poster;

public class UppercaseAdvancedFontMetrics extends AdvancedFontMetrics {

	@Override
	public float getTextHeight() {
		return Math.abs(this.top + this.bottom); // Works without underlining. (Remove +bottom for all CAPS)
	}

	@Override
	public float getUnderlinedTextHeight() {
		return Math.abs(this.top + this.bottom) + this.bottom; // I get the underline height with this but it's shifted down some.
	}
}
