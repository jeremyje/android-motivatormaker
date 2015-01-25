package com.futonredemption.makemotivator.util;

import org.beryl.app.AndroidVersion;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Vibrator;

public class HardwareUtils {
	
	static interface IHardwareUtils {
		boolean supportsMultiTouch(Context context);
	}
	
	private static final IHardwareUtils hardwareUtils;
	static {
		if(AndroidVersion.isEclairOrHigher()) {
			hardwareUtils = new EclairHardwareUtils();
		} else {
			hardwareUtils = new BaseHardwareUtils();
		}
	}

	public static void shortVibrate(final Context context) {
		final Vibrator vib = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
		vib.vibrate(150);
	}
	
	public static boolean supportsMultiTouch(final Context context) {
		return hardwareUtils.supportsMultiTouch(context);
	}
	

	static class BaseHardwareUtils implements IHardwareUtils {
		public boolean supportsMultiTouch(final Context context) {
			return false;
		}
	}
	
	static class EclairHardwareUtils implements IHardwareUtils {
		@TargetApi(5)
		public boolean supportsMultiTouch(final Context context) {
			final PackageManager pm = context.getPackageManager();
			return pm.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH);
		}
	}
}
