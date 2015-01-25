package com.futonredemption.makemotivator.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.beryl.diagnostics.ExceptionReporter;
import org.beryl.graphics.BitmapWrapper;
import org.beryl.media.MediaRegisteredReceiver;
import org.beryl.media.RegisterMediaIntentService;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.futonredemption.makemotivator.Constants;
import com.futonredemption.makemotivator.R;
import com.futonredemption.makemotivator.activities.WebGalleryUploadActivity;
import com.futonredemption.makemotivator.analytics.Analytics;
import com.futonredemption.makemotivator.analytics.CustomVariablesFactory;
import com.futonredemption.makemotivator.contracts.IAnalyticsCommand;
import com.futonredemption.makemotivator.contracts.ICustomizerChangedEvent;
import com.futonredemption.makemotivator.fragments.MotivatorComposerFragment.UiPosterComposition;
import com.futonredemption.makemotivator.poster.PosterComposition;
import com.futonredemption.makemotivator.poster.PosterRenderer;
import com.futonredemption.makemotivator.util.Cancellable;
import com.futonredemption.makemotivator.util.ColorUtils;

final class MotivatorComposerFragmentController {
	private final MotivatorComposerFragment fragment;
	PosterComposition composition = new PosterComposition();
	private AtomicBoolean allowLocalEdits = new AtomicBoolean(true);
	private static final String STATE_Composition = "composition";

	public MotivatorComposerFragmentController(MotivatorComposerFragment fragment) {
		this.fragment = fragment;
	}

	public void setupAnalyticsCustomVariables() {
		final ArrayList<IAnalyticsCommand> cmds = getContracts(IAnalyticsCommand.class);
		for(IAnalyticsCommand cmd : cmds) {
			CustomVariablesFactory.setupPosterComposition(cmd, composition);
		}
	}

	public void blockLocalEdits() {
		allowLocalEdits.set(false);
	}
	
	public void unblockLocalEdits() {
		allowLocalEdits.set(true);
	}
	
	public boolean isLocalEditsAllowed() {
		return allowLocalEdits.get();
	}
	
	public <T> ArrayList<T> getContracts(Class<T> clazz) {
		return fragment.getContracts(clazz);
	}
	
	protected Activity getFragmentActivity() {
		return fragment.getActivity();
	}

	private UiPosterComposition getFragmentComposition() {
		return fragment.composition;
	}

	public void restoreDisplay() {
		compositionToUi();
	}

	public void trackerEventFail(String category, String action, String label) {
		fragment.trackerEventFail(category, action, label);
	}

	public void trackerEventSuccess(String category, String action, String label) {
		fragment.trackerEventSuccess(category, action, label);
	}
	
	public CharSequence getText(int stringResId) {
		return fragment.getText(stringResId);
	}
	private final Object monitorLock = new Object();

	protected DominantColorFigureOuterAsyncTask currentDominantColorFinder = null;
	public void acquirePicture(Bitmap picture) {
		if (picture != null) {
			setPicture(picture);

			synchronized(monitorLock) {
				if(currentDominantColorFinder != null) {
					currentDominantColorFinder.cancel(true);
					currentDominantColorFinder = null;
				}
				currentDominantColorFinder = new DominantColorFigureOuterAsyncTask();
				currentDominantColorFinder.execute(picture);
			}
		}
	}
	
	public void setPicture(Bitmap picture) {
		if (picture != null) {
			final UiPosterComposition fragComp = getFragmentComposition();
			fragComp.setNewImageBitmap(picture);
			fragComp.setIsBlankImage(false);
		}
	}

	public class DominantColorFigureOuterAsyncTask extends AsyncTask<Bitmap, Void, Integer> implements Cancellable {

		@Override
		protected Integer doInBackground(Bitmap... params) {
			int dominantColor = Color.WHITE;

			for(Bitmap bitmap : params) {
				dominantColor = ColorUtils.findDominantColor(bitmap, this);
				dominantColor = ColorUtils.brightenIfTooDark(dominantColor);
			}

			return dominantColor;
		}

		@Override
		protected void onPostExecute(Integer result) {
			
			if(isLocalEditsAllowed()) {
				final UiPosterComposition fragComp = getFragmentComposition();
				final int finalColor = result.intValue();
				fragComp.setForeColor(finalColor);
				
				final ArrayList<ICustomizerChangedEvent> handlers = getContracts(ICustomizerChangedEvent.class);
				for(ICustomizerChangedEvent handler : handlers) {
					handler.onForecolorChanged(finalColor);
				}
			}
			currentDominantColorFinder = null;
		}
	}

	public void uiToComposition() {
		PosterComposition.copyTo(getFragmentComposition(), composition);
	}

	public void compositionToUi() {
		PosterComposition.copyTo(composition, getFragmentComposition());
	}

	public void restoreState(Bundle savedInstanceState) {
		composition = savedInstanceState.getParcelable(STATE_Composition);
	}

	public void saveState(Bundle outState) {
		uiToComposition();
		outState.putParcelable(STATE_Composition, composition);
	}

	abstract class PosterWriter extends AsyncTask<String, Void, File> {

		public String overrideFileFormat = null;
		protected MediaRegisteredReceiver getReceiver() {
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			uiToComposition();
			if(overrideFileFormat != null) {
				composition.setFileFormat(overrideFileFormat);
			}
			fragment.WaitingDialog.show(R.string.ind_making_poster, R.string.ind_please_wait);
		}

		@Override
		protected File doInBackground(String... params) {
			File posterFile = null;
			try {
				posterFile = PosterRenderer.savePoster(getFragmentActivity(), composition);
				
				if(posterFile != null) {
					
					MediaRegisteredReceiver receiver = getReceiver();
					if(receiver != null) {
						MediaRegisteredReceiver.registerReceiver(getFragmentActivity(), receiver);
					}
	
					setupAnalyticsCustomVariables();
					RegisterMediaIntentService.addFileToAndroidMediaCollection(getFragmentActivity(), posterFile.getPath());
					trackerEventSuccess(Analytics.Category.Task, Analytics.Action.WritePoster, Analytics.Label.EndInvoke);
				} else {
					trackerEventFail(Analytics.Category.Task, Analytics.Action.WritePoster, Analytics.Label.Invalid);
				}
				
			} catch (Exception e) {
				ExceptionReporter.report(e);
				String msg = e.getMessage();
				if(msg == null) {
					msg = e.getClass().getName();
				} 
				if(msg == null) {
					msg = Analytics.Label.ExceptionThrown;
				}
				
				trackerEventFail(Analytics.Category.Task, Analytics.Action.WritePoster, msg);
			}
			
			return posterFile;
		}

		@Override
		protected void onPostExecute(File posterFile) {
			fragment.WaitingDialog.dismiss();
			if(posterFile != null) {
				final Intent intent = createIntent(posterFile);
				if(intent != null) {
					getFragmentActivity().startActivity(intent);
				}
			} else {
				showToast(R.string.please_turn_off_usb_storage);
				trackerEventFail(Analytics.Category.Task, Analytics.Action.WritePoster, Analytics.Label.NoStorage);
			}
		}
		
		protected Intent createIntent(File posterFile) {
			final Intent shareImage = new Intent(Intent.ACTION_SEND);
			//shareImage.addCategory(Intent.CATEGORY_DEFAULT);
			shareImage.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });

			shareImage.putExtra(Intent.EXTRA_SUBJECT, composition.getTitle());
			shareImage.putExtra(Intent.EXTRA_TEXT, getExtendedMessageBody());
			shareImage.putExtra("sms_body", getMessageBody());
			Uri uri = Uri.fromFile(posterFile);
			shareImage.setType("image/*");
			shareImage.putExtra(Intent.EXTRA_STREAM, uri);

			return shareImage;
		}
		
		protected String getExtendedMessageBody() {
			return getMessageBody() + "\nBy Motivator Maker goo.gl/hMzam";
		}
		
		protected String getMessageBody() {
			return composition.getTitle() + " - " + composition.getSubtitle();
		}
	}

	class SavePosterWriter extends PosterWriter {
		@Override
		protected Intent createIntent(File posterFile) {
			final Intent viewImage = new Intent(Intent.ACTION_VIEW);
			viewImage.addCategory(Intent.CATEGORY_DEFAULT);
			Uri uri = Uri.fromFile(posterFile);
			viewImage.setDataAndType(uri, "image/*");

			final Intent runViewImage = Intent.createChooser(viewImage, getText(R.string.view_poster));
			return runViewImage;
		}
	}

	class SharedPosterWriter extends PosterWriter {
		
		private final String packageName;
		private final String activityName;
		
		public SharedPosterWriter() {
			super();
			this.packageName = null;
			this.activityName = null;
		}
		
		public boolean isSpecificIntent() {
			return this.packageName != null && this.activityName != null;
		}
		
		public SharedPosterWriter(final String packageName, final String activityName) {
			this.packageName = packageName;
			this.activityName = activityName;
		}
		
		@Override
		protected MediaRegisteredReceiver getReceiver() {
			MediaRegisteredReceiver receiver = new MediaRegisteredReceiver() {
				
				@Override
				protected void onScanCompleted(String path, Uri uri) {
					final Intent shareImage = new Intent(Intent.ACTION_SEND);
					shareImage.addCategory(Intent.CATEGORY_DEFAULT);
					shareImage.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });

					shareImage.putExtra(Intent.EXTRA_SUBJECT, composition.getTitle());
					shareImage.putExtra(Intent.EXTRA_TEXT, getExtendedMessageBody());
					shareImage.putExtra("sms_body", getMessageBody());
					shareImage.setType("image/*");
					shareImage.putExtra(Intent.EXTRA_STREAM, uri);
					
					// Extras for the Web Gallery.
					shareImage.putExtra("title", composition.getTitle());
					shareImage.putExtra("subtitle", composition.getSubtitle());
					shareImage.putExtra("frameColor", composition.getBorderColor());
					shareImage.putExtra("filePath", path);
					
					Intent runShareImage;
					
					if(SharedPosterWriter.this.isSpecificIntent()) {
						shareImage.setClassName(SharedPosterWriter.this.packageName, SharedPosterWriter.this.activityName);
						runShareImage = shareImage;
					} else {
						runShareImage = Intent.createChooser(shareImage, getText(R.string.share_poster));
					}
					
					getFragmentActivity().startActivity(runShareImage);
					getFragmentActivity().unregisterReceiver(this);
				}
				
				protected String getExtendedMessageBody() {
					return getMessageBody() + "\nBy Motivator Maker " + Constants.Exif.MarketLink;
				}
				
				protected String getMessageBody() {
					return composition.getTitle() + " - " + composition.getSubtitle();
				}
			};
			
			return receiver;
		}
		
		@Override
		protected Intent createIntent(File posterFile) {
			/*
			final Intent shareImage = new Intent(Intent.ACTION_SEND);
			shareImage.addCategory(Intent.CATEGORY_DEFAULT);
			shareImage.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });

			shareImage.putExtra(Intent.EXTRA_SUBJECT, composition.getTitle());
			shareImage.putExtra(Intent.EXTRA_TEXT, composition.getTitle() + " - " + composition.getSubtitle());
			shareImage.putExtra("sms_body", composition.getTitle() + " - " + composition.getSubtitle());
			Uri uri = Uri.fromFile(posterFile);
			shareImage.setType("image/*");
			shareImage.putExtra(Intent.EXTRA_STREAM, uri);

			final Intent runShareImage = Intent.createChooser(shareImage, getText(R.string.share_poster));
			
			return runShareImage;
			*/
			return null;
		}
	}

	public void sharePoster() {
		try {
			getFragmentComposition().loadPreferences();
			getFragmentComposition().validateFields();
			new SharedPosterWriter().execute();
		} catch(Exception e) {
			trackerEventFail(Analytics.Category.Command, Analytics.Action.SharePoster, Analytics.Label.ExceptionThrown);
			showToast(e.getMessage());
		}
	}
	
	public void sharePoster(final String packageName, final String activityName) {
		try {
			getFragmentComposition().loadPreferences();
			getFragmentComposition().validateFields();
			new SharedPosterWriter(packageName, activityName).execute();
		} catch(Exception e) {
			trackerEventFail(Analytics.Category.Command, Analytics.Action.SharePoster, Analytics.Label.ExceptionThrown);
			showToast(e.getMessage());
		}
	}

	public void savePoster() {
		try {
			getFragmentComposition().loadPreferences();
			getFragmentComposition().validateFields();
			new SavePosterWriter().execute();
		} catch(Exception e) {
			trackerEventFail(Analytics.Category.Command, Analytics.Action.SavePoster, Analytics.Label.ExceptionThrown);
			showToast(e.getMessage());
		}
	}
	
	public void showToast(int stringId) {
		Toast.makeText(getFragmentActivity(), stringId, Toast.LENGTH_LONG).show();
	}
	
	public void showToast(CharSequence message) {
		Toast.makeText(getFragmentActivity(), message, Toast.LENGTH_LONG).show();
	}

	public void rotateImage() {
		final UiPosterComposition fragComp = getFragmentComposition();
		final Bitmap original = fragComp.getImageBitmap();

		if(BitmapWrapper.isUsable(original)) {
			final Bitmap rotated = BitmapWrapper.rotateBitmap(original, 90);
			setPicture(rotated);
			BitmapWrapper.dispose(original);
		}
	}

	public void uploadPoster() {
		String activityName = WebGalleryUploadActivity.class.getName();
		String packageName = getFragmentActivity().getPackageName();
		this.sharePoster(packageName, activityName);
	}

	public void setHasBlankImage() {
		final UiPosterComposition fragComp = getFragmentComposition();
		fragComp.setIsBlankImage(true);
	}
}
