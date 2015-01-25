package com.futonredemption.makemotivator.fragments;

import org.beryl.graphics.BitmapWrapper;
import org.beryl.intents.ActivityIntentLauncher;
import org.beryl.intents.ActivityResultTask;
import org.beryl.intents.IActivityResultHandler;
import org.beryl.intents.android.Camera;
import org.beryl.intents.android.Gallery;
import org.beryl.io.Storage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.futonredemption.makemotivator.Constants;
import com.futonredemption.makemotivator.R;
import com.futonredemption.makemotivator.analytics.Analytics;
import com.futonredemption.makemotivator.contracts.ICustomizableCommand;
import com.futonredemption.makemotivator.contracts.ICustomizerChangedEvent;
import com.futonredemption.makemotivator.contracts.IPosterCommand;
import com.futonredemption.makemotivator.poster.IPosterComposition;
import com.futonredemption.makemotivator.poster.IPosterElementFactory;
import com.futonredemption.makemotivator.poster.PosterFactoryResolver;
import com.futonredemption.makemotivator.poster.PosterPreferenceModel;
import com.futonredemption.makemotivator.poster.PreferenceValueConvert;
import com.futonredemption.makemotivator.poster.PosterThemeBinder;
import com.futonredemption.makemotivator.poster.measure.MeasureParams;
import com.futonredemption.makemotivator.util.DrawableUtils;
import com.futonredemption.makemotivator.util.FontRenderer;
import com.futonredemption.makemotivator.util.FontUtils;
import com.futonredemption.makemotivator.widget.DetachableProgressDialog;
import com.futonredemption.makemotivator.widget.PanZoomImageView;
import com.futonredemption.makemotivator.widget.ScaledCompositeLayout;
import com.sonyericsson.zoom.ZoomState;

public class MotivatorComposerFragment extends FragmentBase {

	static final int ACTIVITYRESULT_CHOOSEPICTURE = 1;
	static final int ACTIVITYRESULT_TAKEPICTURE = 2;
	final MotivatorComposerFragmentController controller = new MotivatorComposerFragmentController(this);

	public DetachableProgressDialog WaitingDialog = new DetachableProgressDialog();

	protected ScaledCompositeLayout PosterRootScaledCompositeLayout;
	
	protected EditText TitleEditText;
	protected EditText SubtitleEditText;
	protected PanZoomImageView MotivatorPicturePanZoomImageView;
	protected View PictureFrameView;
	protected View PictureFramePaddingView;
	protected RelativeLayout PanZoomPictureRelativeLayout;
	

	final UiPosterComposition composition = new UiPosterComposition();
	ActivityIntentLauncher intentLauncher = new ActivityIntentLauncher();

	private static final String STATE_IntentActivityLauncher = "IntentActivityLauncher";

	private boolean queueLoadDefaultGraphic = true;

	@Override
	protected int getFragmentLayoutId() {
		return R.layout.fragment_motivatorcomposer;
	}

	@Override
	protected void onCreateView(View view) {
		bindViews(view);
		attachListeners();
		setupViews();
	}
	
	private void bindViews(View view) {
		PosterRootScaledCompositeLayout = (ScaledCompositeLayout)view.findViewById(R.id.PosterRootScaledCompositeLayout);
		TitleEditText = (EditText)view.findViewById(R.id.TitleEditText);
		SubtitleEditText = (EditText)view.findViewById(R.id.SubtitleEditText);
		MotivatorPicturePanZoomImageView = (PanZoomImageView)view.findViewById(R.id.MotivatorPicturePanZoomImageView);
		PictureFrameView = (View)view.findViewById(R.id.PictureFrameView);
		PictureFramePaddingView = (View)view.findViewById(R.id.PictureFramePaddingView);
		PanZoomPictureRelativeLayout = (RelativeLayout)view.findViewById(R.id.PanZoomPictureRelativeLayout);
	}

	private void setupViews() {
		TitleEditText.setTypeface(FontUtils.getCaslon540Bt(getActivity()));
		FontRenderer.getRecommendedTextSize(null, null, null);

		if(queueLoadDefaultGraphic) {
			this.setBlankPicture();
		}

		composition.themeToUi();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		// TODO: Do this async.
		composition.loadPreferences();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		WaitingDialog.onAttach(activity);
		intentLauncher.setActivityLauncher(launcherProxy);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		WaitingDialog.onDetach();
		intentLauncher.setActivityLauncher(null);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRetainInstance(true);
		queueLoadDefaultGraphic = true;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if(savedInstanceState != null) {
			restoreFragment(savedInstanceState);
		} else {
			controller.restoreDisplay();
		}
	}

	private void restoreFragment(Bundle savedInstanceState) {
		intentLauncher = savedInstanceState.getParcelable(STATE_IntentActivityLauncher);

		intentLauncher.setActivityLauncher(launcherProxy);
		controller.restoreState(savedInstanceState);
		controller.restoreDisplay();
	}

	private void saveFragment(Bundle outState) {
		controller.saveState(outState);
		outState.putParcelable(STATE_IntentActivityLauncher, intentLauncher);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveFragment(outState);
	}

	private void attachListeners() {
		SubtitleEditText.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
					InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(SubtitleEditText.getWindowToken(), 0);

					return true;
				}
				return false;
			}
		});
	}

	public boolean checkStorageState() {
		boolean mounted = Storage.isExternalStorageAvailable();
		if(! mounted) {
			Toast.makeText(getActivity(), R.string.please_turn_off_usb_storage, Toast.LENGTH_LONG).show();
		}
		return mounted;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		IActivityResultHandler handler = null;

		if(requestCode == ACTIVITYRESULT_CHOOSEPICTURE) {
			handler = new Gallery.GetImageResult() {

				public void onResultCompleted() {
					if(this.bitmapResult.isAvailable()) {
						trackerEventSuccess(Analytics.Category.LaunchIntent, Analytics.Action.Gallery, Analytics.Label.EndInvoke);
						acquirePicture(this.bitmapResult.get());
					} else {
						trackerEventFail(Analytics.Category.LaunchIntent, Analytics.Action.Gallery, Analytics.Label.EndInvoke);
						Toast.makeText(getActivity(), R.string.cannot_load_picture, Toast.LENGTH_LONG).show();
					}
					this.bitmapResult = null;
				}

				public void onResultCanceled() {
					this.bitmapResult = null;
				}

				public void onResultCustomCode(int resultCode) {
					this.bitmapResult = null;
				}
			};

		} else if(requestCode == ACTIVITYRESULT_TAKEPICTURE) {
			handler = new Camera.GetCameraCaptureResult() {

				public void onResultCanceled() {
					this.bitmapResult = null;
				}

				public void onResultCompleted() {
					if(this.bitmapResult.isAvailable()) {
						trackerEventSuccess(Analytics.Category.LaunchIntent, Analytics.Action.Camera, Analytics.Label.EndInvoke);
						acquirePicture(this.bitmapResult.get());
					} else {
						trackerEventFail(Analytics.Category.LaunchIntent, Analytics.Action.Camera, Analytics.Label.EndInvoke);
						Toast.makeText(getActivity(), R.string.cannot_load_picture, Toast.LENGTH_LONG).show();
					}
					this.bitmapResult = null;
				}

				public void onResultCustomCode(int resultCode) {
					this.bitmapResult = null;
				}
			};
		}

		if(handler != null) {
			final ActivityResultTask resultTask = new ActivityResultTask(intentLauncher, handler, resultCode, data);
			resultTask.execute();
		}
	}

	public void showToast(int stringResId) {
		Toast.makeText(getActivity(), stringResId, Toast.LENGTH_LONG).show();
	}

	final ActivityIntentLauncher.IActivityLauncherProxy launcherProxy = new ActivityIntentLauncher.IActivityLauncherProxy() {
		public Activity getContext() {
			return MotivatorComposerFragment.this.getActivity();
		}

		public void startActivityForResult(Intent intent, int requestCode) {
			MotivatorComposerFragment.this.startActivityForResult(intent, requestCode);
		}

		public void startActivity(Intent intent) {
			MotivatorComposerFragment.this.startActivity(intent);
		}

		public void onStartActivityFailed(Intent intent) {
			MotivatorComposerFragment.this.showToast(R.string.nolocal_generic_error);
		}

		public void onStartActivityForResultFailed(Intent intent, int requestCode) {
			MotivatorComposerFragment.this.showToast(R.string.nolocal_generic_error);
		}
	};

	public void loadFromLaunchIntent(Intent data) {
		Gallery.SendImageResult handler = new Gallery.SendImageResult() {

			public void onResultCompleted() {
				if(this.bitmapResult.isAvailable()) {
					trackerEventSuccess(Analytics.Category.LaunchMode, Analytics.Action.FromSendIntent, Analytics.Label.EndInvoke);
					acquirePicture(this.bitmapResult.get());
				} else {
					trackerEventFail(Analytics.Category.LaunchMode, Analytics.Action.FromSendIntent, Analytics.Label.EndInvoke);
					Toast.makeText(getActivity(), R.string.cannot_load_picture, Toast.LENGTH_LONG).show();
				}
				this.bitmapResult = null;
			}

			public void onResultCanceled() {
				this.bitmapResult = null;
			}

			public void onResultCustomCode(int resultCode) {
				this.bitmapResult = null;
			}
		};

		final ActivityResultTask resultTask = new ActivityResultTask(intentLauncher, handler, Activity.RESULT_OK, data);
		resultTask.execute();
	}

	public void setBlankPicture() {
		final Bitmap blankPicture = BitmapFactory.decodeResource(this.getResources(), R.drawable.no_picture);
		controller.acquirePicture(blankPicture);
		controller.setHasBlankImage();
		composition.setForeColor(Color.WHITE);
		queueLoadDefaultGraphic = false;
		controller.uiToComposition();
	}

	public void acquirePicture(Bitmap picture) {
		controller.unblockLocalEdits();
		controller.acquirePicture(picture);
	}

	public class UiPosterComposition implements IPosterComposition {

		String themeTag = Constants.Preferences.DefaultValue_PosterTheme;
		String fileFormat = CompressFormat.PNG.toString();
		float sizeMultiplier = 1.0F;
		boolean isBlankImage = true;
		
		public int getForeColor() {
			return getBorderColor();
		}

		public void setForeColor(int color) {
			setTitleColor(color);
			setBorderColor(color);
		}

		public int getBorderColor() {
			return DrawableUtils.getBackgroundColor(PictureFrameView);
		}

		public Bitmap getImageBitmap() {
			return MotivatorPicturePanZoomImageView.getBitmap();
		}

		public String getSubtitle() {
			return SubtitleEditText.getText().toString().trim();
		}

		public int getSubtitleColor() {
			return SubtitleEditText.getCurrentTextColor();
		}

		public String getThemeTag() {
			return themeTag;
		}

		public String getTitle() {
			return TitleEditText.getText().toString().trim();
		}

		public int getTitleColor() {
			return TitleEditText.getCurrentTextColor();
		}

		public void setBorderColor(int borderColor) {
			PictureFrameView.setBackgroundColor(borderColor);
		}

		public void setNewImageBitmap(Bitmap picture) {
			final Bitmap oldBitmap = MotivatorPicturePanZoomImageView.getBitmap();
			MotivatorPicturePanZoomImageView.setAndScaleImageBitmap(picture);

			// FIXME: Find where the real root of the memory leak is.
			if(oldBitmap != picture) {
				BitmapWrapper.dispose(oldBitmap);
			}
		}

		public void setImageBitmap(Bitmap picture) {
			MotivatorPicturePanZoomImageView.setImageBitmap(picture);
		}

		public void setSubtitle(String subtitle) {
			SubtitleEditText.setText(subtitle);
		}

		public void setSubtitleColor(int subtitleColor) {
			SubtitleEditText.setTextColor(subtitleColor);
		}

		public void setThemeTag(String themeTag) {
			if(! this.themeTag.equals(themeTag)) {
				this.themeTag = themeTag;
				themeToUi();
			}
		}

		public void setTitle(String title) {
			TitleEditText.setText(title);
		}

		public void setTitleColor(int titleColor) {
			TitleEditText.setTextColor(titleColor);
		}

		public float getImageBitmapPanX() {
			return getZoomState().getPanX();
		}

		public float getImageBitmapPanY() {
			return getZoomState().getPanY();
		}

		public float getImageBitmapScale() {
			return getZoomState().getZoom();
		}

		public void setImageBitmapPanX(float imageBitmapPanX) {
			final ZoomState panZoom = getZoomState();
			panZoom.setPanX(imageBitmapPanX);
		}

		public void setImageBitmapPanY(float imageBitmapPanY) {
			final ZoomState panZoom = getZoomState();
			panZoom.setPanY(imageBitmapPanY);
		}

		public void setImageBitmapScale(float imageBitmapScale) {
			final ZoomState panZoom = getZoomState();
			panZoom.setZoom(imageBitmapScale);
		}

		private ZoomState getZoomState() {
			return MotivatorPicturePanZoomImageView.getZoomState();
		}

		public MeasureParams getMeasureParams() {
			int orientation;

			if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				orientation = MeasureParams.ORIENTATION_Landscape;
			} else {
				orientation = MeasureParams.ORIENTATION_Portrait;
			}

			final MeasureParams params = new MeasureParams(1.0F, orientation);
			return params;
		}

		public void setMeasureParams(MeasureParams params) {
		}

		public void validateFields() throws Exception {
			if(safeStringLength(getTitle()) < 4) {
				TitleEditText.requestFocus();
				throw new Exception(getText(R.string.title_needs_to_be_longer).toString());
			}

			if(safeStringLength(getSubtitle()) < 4) {
				SubtitleEditText.requestFocus();
				throw new Exception(getText(R.string.subtitle_needs_to_be_longer).toString());
			}
			
			// Valid picture
			if(this.getIsBlankImage()) {
				throw new Exception(getText(R.string.you_need_a_picture).toString());
			}
		}

		private int safeStringLength(String str) {
			if(str == null) return 0;
			return str.length();
		}

		public void themeToUi() {
			PosterThemeBinder binder = new PosterThemeBinder();
			PosterFactoryResolver resolver = new PosterFactoryResolver();
			IPosterElementFactory factory = resolver.getFactory(themeTag);

			binder.setBase(PosterRootScaledCompositeLayout, factory.createRootElement());
			binder.bind(PictureFrameView, factory.createPictureBorderElement());
			binder.bind(PanZoomPictureRelativeLayout, factory.createPictureGraphicElement());
			binder.bind(PictureFramePaddingView, factory.createPicturePaddingElement());
			binder.bind(SubtitleEditText, factory.createSubtitleElement());
			binder.bind(TitleEditText, factory.createTitleElement());

			final boolean isTabletFormFactor = isTabletFormFactor();
			binder.resizeLayout(! isTabletFormFactor, getMeasureParams());
		}

		public String getFileFormat() {
			return fileFormat;
		}

		public void setFileFormat(String fileFormat) {
			this.fileFormat = fileFormat;
		}
		
		public float getSizeMultiplier() {
			return this.sizeMultiplier;
		}

		public void setSizeMultiplier(float sizeMultiplier) {
			this.sizeMultiplier = sizeMultiplier;
		}

		public void loadPreferences() {
			PosterPreferenceModel model = PosterPreferenceModel.loadPreferences(getActivity());
			
			setFileFormat(model.FileFormat);
			setThemeTag(model.PosterTheme);
			
			final float posterScale = PreferenceValueConvert.getScaleFactor(model);
			setSizeMultiplier(posterScale);
		}

		@Override
		public boolean getIsBlankImage() {
			return this.isBlankImage;
		}

		@Override
		public void setIsBlankImage(boolean isBlank) {
			this.isBlankImage = isBlank;
		}
	}

	/* Externed Contracts */

	final ICustomizerChangedEvent contractICustomizerChangedEvent = new ICustomizerChangedEvent() {
		public void onForecolorChanged(final int color) {
			composition.setForeColor(color);
		}
	};
	
	final ICustomizableCommand fragmentICustomizableCommand = new ICustomizableCommand() {
		public int getForecolor() {
			return composition.getForeColor();
		}
		public void blockLocalEdits() {
			controller.blockLocalEdits();
		}
		public void unblockLocalEdits() {
			controller.unblockLocalEdits();
		}
	};

	final IPosterCommand fragmentIPosterCommand = new IPosterCommand() {
		public void savePoster() {
			if(checkStorageState()) {
				controller.savePoster();
			} else {
				trackerEventFail(Analytics.Category.Command, Analytics.Action.SavePoster, Analytics.Label.NoStorage);
			}
		}
		
		public void invokeShareDialog() {
			if(checkStorageState()) {
				final SharePosterDialogBuilder builder = new SharePosterDialogBuilder(getActivity());
				builder.show(this);
			} else {
				trackerEventFail(Analytics.Category.Command, Analytics.Action.SharePoster, Analytics.Label.NoStorage);
			}
		}

		public void sharePoster() {
			if(checkStorageState()) {
				controller.sharePoster();
			} else {
				trackerEventFail(Analytics.Category.Command, Analytics.Action.SharePoster, Analytics.Label.NoStorage);
			}
		}

		public void sharePoster(String packageName, String activityName) {
			if(checkStorageState()) {
				controller.sharePoster(packageName, activityName);
			} else {
				trackerEventFail(Analytics.Category.Command, Analytics.Action.SharePoster, Analytics.Label.NoStorage);
			}
		}
		
		public void uploadPoster() {
			if(checkStorageState()) {
				controller.uploadPoster();
			} else {
				trackerEventFail(Analytics.Category.Command, Analytics.Action.UploadPoster, Analytics.Label.NoStorage);
			}
		}
		
		public void rotatePicture() {
			trackerEventSuccess(Analytics.Category.Command, Analytics.Action.RotateImage, Analytics.Label.FromMenu);
			controller.rotateImage();
		}

		public void selectPictureFromGallery() {
			if(checkStorageState()) {
				intentLauncher.beginStartActivity(new Gallery.GetImage(), ACTIVITYRESULT_CHOOSEPICTURE);
				trackerEventSuccess(Analytics.Category.LaunchIntent, Analytics.Action.Gallery, Analytics.Label.BeginInvoke);
			} else {
				trackerEventSuccess(Analytics.Category.LaunchIntent, Analytics.Action.Gallery, Analytics.Label.NoStorage);
			}
		}

		public void takePictureFromCamera() {
			if(checkStorageState()) {
				Camera.CaptureRequest captureRequest = new Camera.CaptureRequest();
				captureRequest.title = "Motivator Picture";
				captureRequest.description = "Motivational Poster Picture";
				captureRequest.displayName = "Motivator Picture";
				captureRequest.fileName = "motivator_picture";
				intentLauncher.beginStartActivity(captureRequest, ACTIVITYRESULT_TAKEPICTURE);
				trackerEventSuccess(Analytics.Category.LaunchIntent, Analytics.Action.Camera, Analytics.Label.BeginInvoke);
			} else {
				trackerEventSuccess(Analytics.Category.LaunchIntent, Analytics.Action.Camera, Analytics.Label.NoStorage);
			}
		}
	};
}
