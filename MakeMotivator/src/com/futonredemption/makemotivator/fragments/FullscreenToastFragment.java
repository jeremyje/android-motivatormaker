package com.futonredemption.makemotivator.fragments;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.futonredemption.makemotivator.R;

public class FullscreenToastFragment extends FragmentBase {

	RelativeLayout FullscreenToastFragmentRoot;
	Button ActionButton;
	
	@Override
	protected int getFragmentLayoutId() {
		return R.layout.fragment_fullscreentoast;
	}

	@Override
	protected void onCreateView(View view) {
		bindViews(view);
		setupViews();
	}
	
	private void bindViews(View view) {
		FullscreenToastFragmentRoot = (RelativeLayout)view.findViewById(R.id.FullscreenToastFragmentRoot);
		ActionButton = (Button)view.findViewById(R.id.ActionButton);
	}

	protected void dismiss() {
		goBack();
	}
	
	protected void onActionClicked() {
		// Do nothing?
	}
	
	private void setupViews() {
		FullscreenToastFragmentRoot.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dismiss();
			}
		});
		
		ActionButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onActionClicked();
				dismiss();
			}
		});
	}
}
