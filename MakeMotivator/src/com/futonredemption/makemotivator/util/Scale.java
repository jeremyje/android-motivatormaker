package com.futonredemption.makemotivator.util;

public class Scale {
	public float scaleX;
	public float scaleY;

	public Scale() {
		this.scaleX = 1.0F;
		this.scaleY = 1.0F;
	}


	public Scale(float scale) {
		this.scaleX = scale;
		this.scaleY = scale;
	}

	public Scale(float scaleX, float scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}
}
