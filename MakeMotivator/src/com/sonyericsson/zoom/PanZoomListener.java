package com.sonyericsson.zoom;

import android.view.View;

public interface PanZoomListener extends View.OnTouchListener {
	void setZoomControl(DynamicZoomControl zoomController);
}
