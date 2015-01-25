package com.futonredemption.makemotivator.fragments;

import java.util.ArrayList;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import com.futonredemption.makemotivator.Constants;
import com.futonredemption.makemotivator.R;
import com.futonredemption.makemotivator.contracts.IGoHomeCommand;

public class AboutAppFragment extends FragmentBase {

	public static final String ABOUTAPP_HtmlAssetPath = "file:///android_asset/about/index.html";
	
	Button CloseButton;
	WebView AboutAppContentsWebView;

	static class AppVersionInfo {

		private final String appName;
		private final String versionName;
		private final int versionCode;

		public String getAppName() {
			return appName;
		}

		public String getVersionName() {
			return versionName;
		}

		public String getVersionCode() {
			return Integer.toString(versionCode);
		}

		AppVersionInfo(Context context) {

			final PackageManager pm = context.getPackageManager();
			PackageInfo pinfo = null;
			try {
				pinfo = pm.getPackageInfo(Constants.Application.PackageName, 0);
			} catch (NameNotFoundException e) {
			}

			appName = context.getString(R.string.app_name);
			if(pinfo != null) {
				versionName = pinfo.versionName;
				versionCode = pinfo.versionCode;
			} else {
				versionName = "";
				versionCode = 0;
			}
		}
	}

	@Override
	protected int getFragmentLayoutId() {
		return R.layout.fragment_aboutapp;
	}

	@Override
	protected void onCreateView(View view) {
		bindViews(view);
		setupViews();
	}
	
	private void bindViews(View view) {
		CloseButton = (Button)view.findViewById(R.id.CloseButton);
		AboutAppContentsWebView = (WebView)view.findViewById(R.id.AboutAppContentsWebView);
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
		
		AboutAppContentsWebView.setBackgroundColor(Color.TRANSPARENT);
		final WebSettings settings = AboutAppContentsWebView.getSettings();
		settings.setSupportZoom(true);
		settings.setJavaScriptEnabled(true);

		AppVersionInfo versionInfo = new AppVersionInfo(getActivity());
		AboutAppContentsWebView.addJavascriptInterface(versionInfo, "AppVersionInfo");
		AboutAppContentsWebView.loadUrl(ABOUTAPP_HtmlAssetPath);
		AboutAppContentsWebView.setBackgroundColor(Color.TRANSPARENT);
	}
}
