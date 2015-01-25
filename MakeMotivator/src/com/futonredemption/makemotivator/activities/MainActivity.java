package com.futonredemption.makemotivator.activities;

import java.util.ArrayList;

import org.beryl.app.AndroidVersion;
import org.beryl.intents.android.Web;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.debug.DebuggableFragmentActivity;
import com.futonredemption.makemotivator.Constants;
import com.futonredemption.makemotivator.R;
import com.futonredemption.makemotivator.analytics.Analytics;
import com.futonredemption.makemotivator.contracts.IActivityController;
import com.futonredemption.makemotivator.contracts.IApplicationActivityCommand;
import com.futonredemption.makemotivator.contracts.ICustomizerChangedEvent;
import com.futonredemption.makemotivator.contracts.IPosterCommand;
import com.futonredemption.makemotivator.fragments.CustomizerFragment;
import com.futonredemption.makemotivator.fragments.FullscreenToastFragment;
import com.futonredemption.makemotivator.fragments.MotivatorComposerFragment;
import com.futonredemption.makemotivator.util.DrawableUtils;
import com.futonredemption.makemotivator.util.HardwareUtils;
import com.futonredemption.makemotivator.webupload.PosterUploadClient;

public class MainActivity extends DebuggableFragmentActivity {

	ImageButton customizerActionView = null;
	MotivatorComposerFragment MotivatorComposerFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		hideNotificationBarIfSpaceNeeded();
		setContentView(R.layout.activity_main);

		// enableBugReporting();
		// startDebugMode();

		setupFragments();

		contractIActivityController.hideVirtualKeyboard();

		// If this is the initial launch of the activity (not a context change)
		// then handle the incoming intent.
		if (savedInstanceState == null) {
			handleIntent();
		}

		findViewById(android.R.id.content).post(new Runnable() {
			@Override
			public void run() {
				checkFirstRun();
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void hideNotificationBarIfSpaceNeeded() {
		if (this.isPhoneFormFactor() && this.isLandscapeOrientation()) {
			hideNotificationBar();
		}
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		boolean handled = false;

		switch (keyCode) {
		case KeyEvent.KEYCODE_CAMERA: {
			trackerEventSuccess(Analytics.Category.LaunchIntent, "Camera",
					"KeyPress");
			HardwareUtils.shortVibrate(this);
			final ArrayList<IPosterCommand> listeners = getContractRegistry()
					.getAll(IPosterCommand.class);
			for (IPosterCommand listener : listeners) {
				listener.takePictureFromCamera();
			}
			handled = true;
		}
			break;
		}
		return handled;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
	}

	private void setupFragments() {
		final FragmentManager fragman = this.getSupportFragmentManager();
		MotivatorComposerFragment = (MotivatorComposerFragment) fragman
				.findFragmentById(R.id.MotivatorComposerFragment);
	}

	private void handleIntent() {
		final Intent intent = getIntent();
		if (intent != null) {
			handleIntent(intent);
		}
	}

	private void handleIntent(final Intent intent) {
		final String action = intent.getAction();

		if (action != null) {
			trackerEventSuccess(Analytics.Category.LaunchMode, action, "");
			if (action.equals(Intent.ACTION_SEND)
					|| action.equals(Intent.ACTION_VIEW)) {
				MotivatorComposerFragment.loadFromLaunchIntent(intent);
			}
		} else {
			trackerEventFail(Analytics.Category.LaunchMode,
					Analytics.Action.Invalid, Analytics.Label.Null);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actionbar_main, menu);
		setupActionBar(menu);
		
		return super.onCreateOptionsMenu(menu);
	}

	@TargetApi(11)
	private void setupActionBar(final Menu menu) {
		if (AndroidVersion.isHoneycombOrHigher()) {
			final ActionBar actionBar = getActionBar();
			setupActionBarTitles(actionBar);
			bindActionViews(menu);
		}
	}

	@TargetApi(11)
	private void bindActionViews(Menu menu) {
		final MenuItem customizerItem = menu.findItem(R.id.MenuItemCustomize);
		View v = customizerItem.getActionView();
		customizerActionView = (ImageButton) v;
		customizerActionView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onOptionsItemSelected(customizerItem);
			}
		});
	}

	@TargetApi(11)
	private void setupActionBarTitles(final ActionBar actionBar) {
		if (isPhoneFormFactor() && !isLandscapeOrientation()) {
			actionBar.setTitle("");
		} else {
			actionBar.setSubtitle(R.string.motivator_subtitle);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.MenuItemCustomize: {
			final ArrayList<IApplicationActivityCommand> cmds = getContractRegistry()
					.getAll(IApplicationActivityCommand.class);
			for (IApplicationActivityCommand cmd : cmds) {
				cmd.toggleCustomizer();
			}
		}
			break;
		case R.id.MenuItemShare: {

			final ArrayList<IPosterCommand> cmds = getContractRegistry()
					.getAll(IPosterCommand.class);
			for (IPosterCommand cmd : cmds) {
				cmd.invokeShareDialog();
			}
		}
			break;
		case R.id.MenuItemSave: {
			final ArrayList<IPosterCommand> cmds = getContractRegistry()
					.getAll(IPosterCommand.class);
			for (IPosterCommand cmd : cmds) {
				cmd.savePoster();
			}
		}
			break;
		case R.id.MenuItemViewWebGallery: {
			final ArrayList<IApplicationActivityCommand> cmds = getContractRegistry()
					.getAll(IApplicationActivityCommand.class);
			for (IApplicationActivityCommand cmd : cmds) {
				cmd.showWebGallery();
			}
		}
			break;
		case R.id.MenuItemUploadToWebGallery: {
			final ArrayList<IPosterCommand> cmds = getContractRegistry()
					.getAll(IPosterCommand.class);
			for (IPosterCommand cmd : cmds) {
				cmd.uploadPoster();
			}
		}
			break;
		case R.id.MenuItemViewTutorial: {
			final ArrayList<IApplicationActivityCommand> cmds = getContractRegistry()
					.getAll(IApplicationActivityCommand.class);
			for (IApplicationActivityCommand cmd : cmds) {
				cmd.showTutorial();
			}
		}
			break;
		case R.id.MenuItemSettings: {
			final ArrayList<IApplicationActivityCommand> cmds = getContractRegistry()
					.getAll(IApplicationActivityCommand.class);
			for (IApplicationActivityCommand cmd : cmds) {
				cmd.showSettings();
			}
		}
			break;
		case R.id.MenuItemAbout: {
			final ArrayList<IApplicationActivityCommand> cmds = getContractRegistry()
					.getAll(IApplicationActivityCommand.class);
			for (IApplicationActivityCommand cmd : cmds) {
				cmd.showAboutApplication();
			}
		}
			break;
		default:
			trackerEventFail(Analytics.Category.Command,
					Analytics.Action.Invalid,
					Integer.toString(item.getItemId()));
			return super.onOptionsItemSelected(item);
		}

		return true;
	}

	final IApplicationActivityCommand contractApplicationActivityCommand = new IApplicationActivityCommand() {

		public void showAboutApplication() {
			final Intent intent = new Intent(MainActivity.this,
					AboutAppActivity.class);
			startActivity(intent);
		}

		public void toggleCustomizer() {
			final FragmentManager fragman = getSupportFragmentManager();

			CustomizerFragment customizer = (CustomizerFragment) fragman
					.findFragmentByTag("CustomizerFragment");

			if (customizer == null) {
				trackerEventSuccess(Analytics.Category.Command,
						Analytics.Action.Customizer, Analytics.Label.FromMenu);
				customizer = new CustomizerFragment();
				final FragmentTransaction trans = fragman.beginTransaction();
				trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				trans.add(R.id.MotivatorComposerFragmentRoot, customizer,
						"CustomizerFragment");
				trans.addToBackStack(null);
				trans.commit();
			} else {
				// Hide the customizer.
				fragman.popBackStack();
			}
		}

		public void showSettings() {
			trackerEventSuccess(Analytics.Category.Command,
					Analytics.Action.Settings, Analytics.Label.FromMenu);

			final Intent intent = new Intent(MainActivity.this,
					MainPreferencesActivity.class);
			MainActivity.this.startActivity(intent);
		}

		@Override
		public void showWebGallery() {
			trackerEventSuccess(Analytics.Category.Command,
					Analytics.Action.ViewWebGallery, Analytics.Label.FromMenu);
			Thread asyncLaunchWebGallery = new Thread(new Runnable() {

				public void run() {

					String clientId = null;
					try {
						final PosterUploadClient client = new PosterUploadClient();
						clientId = client.getClientId(MainActivity.this);
					} catch (Exception e) {
						clientId = null;
					}
					String targetUrl;
					if (clientId == null) {
						targetUrl = Constants.Application.WebGalleryHomePageUrl;
					} else {
						targetUrl = Constants.Application.WebGalleryHomePageUrl
								+ "?clientid=" + clientId;
					}

					final String resultUrl = targetUrl;
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							final Intent intent = Web.viewUrl(resultUrl);
							MainActivity.this.startActivity(intent);
						}
					});
				}
			});
			asyncLaunchWebGallery.start();
		}

		@Override
		public void showTutorial() {
			final Intent intent = new Intent(MainActivity.this,
					TutorialActivity.class);
			startActivity(intent);
		}

		@Override
		public void showOverflowMenu() {
			openOptionsMenu();
		}

		@Override
		public void showNewUserIntro() {
			final FragmentManager fragman = getSupportFragmentManager();

			TutorialFullscreenToastFragment fsToast = (TutorialFullscreenToastFragment) fragman
					.findFragmentByTag("FullscreenToastFragment");

			if (fsToast == null) {
				fsToast = new TutorialFullscreenToastFragment();
				final FragmentTransaction trans = fragman.beginTransaction();
				trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				trans.add(android.R.id.content, fsToast, "FullscreenToastFragment");
				trans.addToBackStack(null);
				trans.commit();
			} else {
				// Hide the customizer.
				fragman.popBackStack();
			}
		}
	};

	final ICustomizerChangedEvent contractICustomizerChangedEvent = new ICustomizerChangedEvent() {
		public void onForecolorChanged(int color) {
			if (AndroidVersion.isHoneycombOrHigher()) {
				if (customizerActionView != null) {
					DrawableUtils.setCustomizerDrawableColor(
							customizerActionView,
							((ImageView) customizerActionView).getDrawable(),
							DrawableUtils.dampenColor(color));
				}
			}
		}
	};

	final IActivityController contractIActivityController = new IActivityController() {

		public void goBack() {
			final FragmentManager fragman = getSupportFragmentManager();
			fragman.popBackStack();
		}

		public void hideVirtualKeyboard() {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(
					new View(MainActivity.this).getWindowToken(), 0);
		}
	};

	public class FinishFirstRunTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			final SharedPreferences prefs = getPreferences();
			Editor editor = prefs.edit();
			editor.putBoolean(Constants.Preferences.Internal_FirstRun, false);
			editor.commit();
			return null;
		}
	}

	class FirstRunTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... arg0) {
			final SharedPreferences prefs = getPreferences();
			boolean isFirstRun = prefs.getBoolean(
					Constants.Preferences.Internal_FirstRun, true);
			return isFirstRun;
		}

		protected void onPostExecute(Boolean isFirstRun) {
			if (isFirstRun && ! MainActivity.this.isFinishing()) {
				final ArrayList<IApplicationActivityCommand> cmds = getContractRegistry()
						.getAll(IApplicationActivityCommand.class);
				for (IApplicationActivityCommand cmd : cmds) {
					cmd.showNewUserIntro();
				}

				new FinishFirstRunTask().execute();
			}
		}
	}

	public static class TutorialFullscreenToastFragment extends FullscreenToastFragment {
		
		@Override
		protected void onActionClicked() {
			final ArrayList<IApplicationActivityCommand> cmds = getContracts(IApplicationActivityCommand.class);
			for (IApplicationActivityCommand cmd : cmds) {
				cmd.showTutorial();
			}
		}
	};
	
	void checkFirstRun() {
		new FirstRunTask().execute();
	}
}
