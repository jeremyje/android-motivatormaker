package com.futonredemption.makemotivator.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.futonredemption.makemotivator.R;
import com.futonredemption.makemotivator.contracts.IGoHomeCommand;

public class TutorialFragment extends FragmentBase {

	Button CloseButton;
	ViewPager TutorialViewPager;
	TutorialViewPagerAdapter viewPagerAdapter;
	Button BackButton;
	Button NextButton;
	
	@Override
	protected int getFragmentLayoutId() {
		return R.layout.fragment_tutorial;
	}

	@Override
	protected void onCreateView(View view) {
		bindViews(view);
		setupViews();
	}
	
	private void bindViews(View view) {
		CloseButton = (Button)view.findViewById(R.id.CloseButton);
		TutorialViewPager = (ViewPager)view.findViewById(R.id.TutorialViewPager);
		viewPagerAdapter = new TutorialViewPagerAdapter(getActivity());
		TutorialViewPager.setAdapter(viewPagerAdapter);
		BackButton = (Button)view.findViewById(R.id.BackButton);
		NextButton = (Button)view.findViewById(R.id.NextButton);
	}

	private void setupViews() {
		
		CloseButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final ArrayList<IGoHomeCommand> listeners = getContracts(IGoHomeCommand.class);
				for(IGoHomeCommand listener : listeners) {
					listener.goHome();
				}
			}
		});
		
		BackButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final int index = TutorialViewPager.getCurrentItem();
				if(index > 0) {
					TutorialViewPager.setCurrentItem(index - 1);
				}
			}
		});
		
		NextButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final int index = TutorialViewPager.getCurrentItem();
				if(index < TutorialViewPager.getAdapter().getCount()) {
					TutorialViewPager.setCurrentItem(index + 1);
				}
			}
		});
	}
	
	static class TutorialViewPagerAdapter extends FragmentPagerAdapter {

		private FragmentActivity activity;
		
		public TutorialViewPagerAdapter(FragmentActivity activity) {
			super(activity.getSupportFragmentManager());
			this.activity = activity;
		}

		@Override
		public Fragment getItem(int position) {
			final Bundle state = new Bundle();
			switch(position) {
				case 0: {
					state.putInt("image", R.drawable.tutorial_upload_to_gallery);
					state.putInt("caption", R.string.tutorial_upload_to_gallery);
					
				} break;
				case 1: {
					state.putInt("image", R.drawable.tutorial_phone_orientation);
					state.putInt("caption", R.string.tutorial_phone_orientation);
					
				} break;
				case 2: {
					state.putInt("image", R.drawable.tutorial_frame_color);
					state.putInt("caption", R.string.tutorial_frame_color);
					
				} break;
				case 3: {
					state.putInt("image", R.drawable.tutorial_options);
					state.putInt("caption", R.string.tutorial_options);
					
				} break;
			}
			
			return Fragment.instantiate(activity, TutorialPageFragment.class.getName(), state);
		}

		@Override
		public int getCount() {
			return 4;
		}
		
	}
	
	public static class TutorialPageFragment extends FragmentBase {
		
		int imageRes;
		int captionRes;
		ImageView TutorialPageImageView;
		TextView TutorialPageCaptionTextView;
		
		public TutorialPageFragment() {
			// Not used, I hope.
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			final Bundle args = getArguments();
			if(args != null) {
				imageRes = args.getInt("image");
				captionRes = args.getInt("caption");
			}
			
		}

		@Override
		protected int getFragmentLayoutId() {
			return R.layout.fragment_tutorialpage;
		}

		@Override
		protected void onCreateView(View view) {
			TutorialPageImageView = (ImageView)view.findViewById(R.id.TutorialPageImageView);
			TutorialPageCaptionTextView = (TextView)view.findViewById(R.id.TutorialPageCaptionTextView);
			TutorialPageImageView.setImageResource(imageRes);
			TutorialPageImageView.invalidate();
			
			TutorialPageCaptionTextView.setText(captionRes);
		}
		
	}
}
