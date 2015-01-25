package com.futonredemption.makemotivator.fragments;

import java.util.ArrayList;

import org.beryl.widget.ColorPickerView;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.futonredemption.makemotivator.R;
import com.futonredemption.makemotivator.contracts.IApplicationActivityCommand;
import com.futonredemption.makemotivator.contracts.ICustomizableCommand;
import com.futonredemption.makemotivator.contracts.ICustomizerChangedEvent;

public class CustomizerFragment extends FragmentBase {

	protected Button CustomizerOkButton;
	protected Button CustomizerCancelButton;
	protected Button CustomizerMoreSettingsButton;
	protected ColorPickerView PosterForecolorColorPickerView;

	CustomizerState state = new CustomizerState();

	private static final String STATE_CustomizerState = "CustomizerState";

	boolean saveChanges = false;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if(savedInstanceState != null) {
			restoreFragment(savedInstanceState);
		} else {
			obtainCustomizations();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveFragment(outState);
	}

	private void restoreFragment(Bundle savedInstanceState) {
		state = savedInstanceState.getParcelable(STATE_CustomizerState);
	}

	private void saveFragment(Bundle outState) {
		outState.putParcelable(STATE_CustomizerState, state);
	}

	@Override
	protected int getFragmentLayoutId() {
		return R.layout.fragment_customizer;
	}

	@Override
	protected void onCreateView(View view) {
		bindViews(view);
		setupViews();
	}
	
	private void bindViews(View view) {
		CustomizerOkButton = (Button)view.findViewById(R.id.CustomizerOkButton);
		CustomizerCancelButton = (Button)view.findViewById(R.id.CustomizerCancelButton);
		CustomizerMoreSettingsButton = (Button)view.findViewById(R.id.CustomizerMoreSettingsButton);
		PosterForecolorColorPickerView = (ColorPickerView)view.findViewById(R.id.PosterForecolorColorPickerView);
	}

	private void setupViews() {
		PosterForecolorColorPickerView.setOnColorChangedListener(forecolorChangedProxy);

		CustomizerOkButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CustomizerFragment.this.saveChanges = true;
				CustomizerFragment.this.closeCustomizer();
			}
		});

		CustomizerCancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CustomizerFragment.this.saveChanges = false;
				CustomizerFragment.this.closeCustomizer();
			}
		});
		
		CustomizerMoreSettingsButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showSettings();
			}
		});
	}

	protected void showSettings() {
		final ArrayList<IApplicationActivityCommand> cmds = getContracts(IApplicationActivityCommand.class);
		for(IApplicationActivityCommand cmd : cmds) {
			cmd.showSettings();
		}
	}
	protected void closeCustomizer() {
		final ArrayList<IApplicationActivityCommand> cmds = getContracts(IApplicationActivityCommand.class);
		for(IApplicationActivityCommand cmd : cmds) {
			cmd.toggleCustomizer();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		blockLocalEdits();
	}

	@Override
	public void onPause() {
		super.onPause();
		if(isRemoving()) {
			applyCustomizations();
		}
	}

	public void obtainCustomizations() {
		final ArrayList<ICustomizableCommand> cmds = getContracts(ICustomizableCommand.class);
		for(ICustomizableCommand cmd : cmds) {
			state.originalForecolor = cmd.getForecolor();
			state.changedForecolor = cmd.getForecolor();
		}
	}

	private void applyCustomizations() {
		if(saveChanges) {
			setCustomizableForecolor(state.changedForecolor);
		} else {
			setCustomizableForecolor(state.originalForecolor);
		}
	}

	public void blockLocalEdits() {
		final ArrayList<ICustomizableCommand> cmds = getContracts(ICustomizableCommand.class);
		for(ICustomizableCommand cmd : cmds) {
			cmd.blockLocalEdits();
		}
	}

	public void setCustomizableForecolor(int color) {
		state.changedForecolor = color;
		final ArrayList<ICustomizerChangedEvent> handlers = getContracts(ICustomizerChangedEvent.class);
		for(ICustomizerChangedEvent handler : handlers) {
			handler.onForecolorChanged(color);
		}
	}

	final ColorPickerView.OnColorChangedListener forecolorChangedProxy = new ColorPickerView.OnColorChangedListener() {
		public void onColorChanged(int color) {
			setCustomizableForecolor(color);
		}
		public void onPreviewColorChanged(int color) {
			setCustomizableForecolor(color);
		}
	};
}
