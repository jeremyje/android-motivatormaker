package com.futonredemption.makemotivator.fragments;

import java.util.ArrayList;

import com.futonredemption.makemotivator.R;
import com.futonredemption.makemotivator.contracts.IPosterCommand;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class PictureControlsFragment extends FragmentBase {

	protected ImageButton PickFromGalleryImageButton;
	protected ImageButton PickFromCameraImageButton;
	protected ImageButton RotateImageImageButton;
	
	@Override
	protected int getFragmentLayoutId() {
		return R.layout.fragment_picturecontrols;
	}

	@Override
	protected void onCreateView(View view) {
		bindViews(view);
		setupViews();
	}

	private void setupViews() {
		if(PickFromGalleryImageButton != null) {
			PickFromGalleryImageButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					final ArrayList<IPosterCommand> handlers = getContracts(IPosterCommand.class);
					for(IPosterCommand handler : handlers) {
						handler.selectPictureFromGallery();
					}
				}
			});
		}

		if(PickFromCameraImageButton != null) {
			PickFromCameraImageButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					final ArrayList<IPosterCommand> handlers = getContracts(IPosterCommand.class);
					for(IPosterCommand handler : handlers) {
						handler.takePictureFromCamera();
					}
				}
			});
		}

		if(RotateImageImageButton != null) {
			RotateImageImageButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					final ArrayList<IPosterCommand> handlers = getContracts(IPosterCommand.class);
					for(IPosterCommand handler : handlers) {
						handler.rotatePicture();
					}
				}
			});
		}
	}

	private void bindViews(View view) {
		PickFromGalleryImageButton = (ImageButton)view.findViewById(R.id.PickFromGalleryImageButton);
		PickFromCameraImageButton = (ImageButton)view.findViewById(R.id.PickFromCameraImageButton);
		RotateImageImageButton = (ImageButton)view.findViewById(R.id.RotateImageImageButton);
	}

}
