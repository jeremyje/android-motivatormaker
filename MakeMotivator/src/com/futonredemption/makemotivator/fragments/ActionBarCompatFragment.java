package com.futonredemption.makemotivator.fragments;

import java.util.ArrayList;

import org.beryl.diagnostics.Logger;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.futonredemption.makemotivator.R;
import com.futonredemption.makemotivator.contracts.IApplicationActivityCommand;
import com.futonredemption.makemotivator.contracts.ICustomizerChangedEvent;
import com.futonredemption.makemotivator.contracts.IPosterCommand;
import com.futonredemption.makemotivator.util.DrawableUtils;

public class ActionBarCompatFragment extends FragmentBase {
	
	protected ImageButton ActionBarCompatSaveImageButton;
	protected ImageButton ActionBarCompatShareImageButton;
	protected ImageButton ActionBarCompatIconImageButton;
	protected ImageButton ActionBarCompatCustomizerImageButton;
	protected ImageButton ActionBarCompatOverflowImageButton;
	
	@Override
	protected int getFragmentLayoutId() {
		return R.layout.fragment_actionbarcompat;
	}

	@Override
	protected void onCreateView(View view) {
		bindViews(view);
		attachListeners();
	}
	
	private void bindViews(View view) {
		ActionBarCompatSaveImageButton = (ImageButton)view.findViewById(R.id.ActionBarCompatSaveImageButton);
		ActionBarCompatShareImageButton = (ImageButton)view.findViewById(R.id.ActionBarCompatShareImageButton);
		ActionBarCompatIconImageButton = (ImageButton)view.findViewById(R.id.ActionBarCompatIconImageButton);
		ActionBarCompatCustomizerImageButton = (ImageButton)view.findViewById(R.id.ActionBarCompatCustomizerImageButton);
		ActionBarCompatOverflowImageButton = (ImageButton)view.findViewById(R.id.ActionBarCompatOverflowImageButton);
	}
	
	private void attachListeners() {
		if(ActionBarCompatSaveImageButton != null) {
			ActionBarCompatSaveImageButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					final ArrayList<IPosterCommand> cmds = getContracts(IPosterCommand.class);
					for(IPosterCommand cmd : cmds) {
						cmd.savePoster();
					}
				}
			});
		}

		if(ActionBarCompatShareImageButton != null) {
			ActionBarCompatShareImageButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					final ArrayList<IPosterCommand> cmds = getContracts(IPosterCommand.class);
					for(IPosterCommand cmd : cmds) {
						cmd.invokeShareDialog();
					}
				}
			});
		}

		if(ActionBarCompatCustomizerImageButton != null) {
			ActionBarCompatCustomizerImageButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					final ArrayList<IApplicationActivityCommand> cmds = getContracts(IApplicationActivityCommand.class);
					for(IApplicationActivityCommand cmd : cmds) {
						cmd.toggleCustomizer();
					}
				}
			});
		}
		
		if(ActionBarCompatIconImageButton != null) {
			ActionBarCompatIconImageButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					final ArrayList<IApplicationActivityCommand> cmds = getContracts(IApplicationActivityCommand.class);
					for(IApplicationActivityCommand cmd : cmds) {
						cmd.showAboutApplication();
					}
				}
			});
		}
		
		if(ActionBarCompatOverflowImageButton != null) {
			ActionBarCompatOverflowImageButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					final ArrayList<IApplicationActivityCommand> cmds = getContracts(IApplicationActivityCommand.class);
					for(IApplicationActivityCommand cmd : cmds) {
						cmd.showOverflowMenu();
					}
				}
			});
		}
	}
	
	final ICustomizerChangedEvent contractICustomizerChangedEvent = new ICustomizerChangedEvent() {
		public void onForecolorChanged(final int color) {
			changeColor(color);
		}
		public void changeColor(final int color) {
			try {
				DrawableUtils.setCustomizerDrawableColor(ActionBarCompatCustomizerImageButton,
						ActionBarCompatCustomizerImageButton.getDrawable(),
						DrawableUtils.dampenColor(color));
			} catch(Exception e) {
				Logger.e(e);
			}
		}
	};
}
