package org.beryl.app.diagnostics.reporting;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.ref.WeakReference;

import org.beryl.diagnostics.NonCriticalThrowableHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.widget.Toast;

public class CrashReporter implements UncaughtExceptionHandler, NonCriticalThrowableHandler {

	private static WeakReference<Application> ApplicationContext = null;
	private static String MetaData_ApplicationName = null;
	private static String MetaData_ApplicationPackageName = null;
	private static String MetaData_ApplicationVersionCode = null;
	private static String MetaData_ApplicationVersionName = null;
	private static String MetaData_EmailTo = null;

	public static void initializeReporter(final Application appContext, final String emailTo) {
		if (CrashReporter.ApplicationContext == null) {
			ApplicationContext = new WeakReference<Application>(appContext);
			final PackageManager pm = appContext.getPackageManager();
			try {
				final String package_name = appContext.getPackageName();
				String app_name = "";
				String version_code = "";
				String version_name = "";
				final PackageInfo pkginfo = pm.getPackageInfo(package_name,
						PackageManager.GET_META_DATA);
				if (pkginfo != null) {
					version_code = Integer.toString(pkginfo.versionCode);
					version_name = pkginfo.versionName;

					final ApplicationInfo appinfo = pm.getApplicationInfo(
							package_name, PackageManager.GET_META_DATA);
					if (appinfo != null) {
						app_name = pm.getApplicationLabel(appinfo).toString();
					}
				}

				CrashReporter.MetaData_ApplicationName = app_name;
				CrashReporter.MetaData_ApplicationPackageName = package_name;
				CrashReporter.MetaData_ApplicationVersionCode = version_code;
				CrashReporter.MetaData_ApplicationVersionName = version_name;
				CrashReporter.MetaData_EmailTo = emailTo;
			} catch (Exception e) {
			}
		}
	}

	public static void bindReporter() {
		bindReporter(Thread.currentThread());
	}

	public static void bindReporter(final Thread thread) {
		try {
			boolean createnew = true;
			int i;
			Thread t;
			int len;
			Thread[] threads;
			UncaughtExceptionHandler ueh;

			ThreadGroup parent = thread.getThreadGroup();
			while (parent.getParent() != null && parent.getParent() != parent) {
				parent = parent.getParent();
			}

			threads = new Thread[parent.activeCount()];
			len = parent.enumerate(threads);

			for (i = 0; i < len; i++) {
				t = threads[i];
				ueh = t.getUncaughtExceptionHandler();
				if (ueh != null) {
					if (ueh.getClass().equals(CrashReporter.class)) {
						createnew = false;
					}
				}
				if (createnew)
					t.setUncaughtExceptionHandler(new CrashReporter(t.getUncaughtExceptionHandler()));
			}
		} catch (Exception e) {
		}
	}

	private final UncaughtExceptionHandler superHandler;

	public static CrashReporter createReporter() {
		return new CrashReporter(Thread.currentThread().getUncaughtExceptionHandler());
	}
	
	public CrashReporter(UncaughtExceptionHandler superHandler) {
		this.superHandler = superHandler;
	}

	public void uncaughtException(Thread thread, Throwable ex) {
		reportError(thread, ex);
		superHandler.uncaughtException(thread, ex);
	}
	
	private void reportError(Throwable ex) {
		reportError(Thread.currentThread(), ex);
	}

	private void reportError(Thread thread, Throwable ex) {
		try {
			final CrashReportParcel parcel = new CrashReportParcel(thread, ex);
			final ICrashParachute parachute = CrashReporter.CreateParachute(parcel);

			if (parachute != null) {
				parachute.deploy();
			}
		} catch (Exception e) {

		}
	}

	private static ICrashParachute CreateParachute(CrashReportParcel parcel) {
		final Context context = CrashReporter.ApplicationContext.get();
		ICrashParachute result = null;

		if(context != null) {
			result = new PendingActivityCrashParachute(parcel, context, CrashReporter.MetaData_EmailTo);
		}

		return result;
	}

	private static final int NotificationId_DisplayActivity = 2312321;

	static class PendingActivityCrashParachute implements ICrashParachute {
		private final CrashReportParcel _parcel;
		private final Context _context;
		private final String emailAddress;

		public PendingActivityCrashParachute(final CrashReportParcel parcel, final Context context, final String emailTo) {
			_parcel = parcel;
			_context = context;
			emailAddress = emailTo;
		}

		private String formatEmailMessage() {
			StringBuilder sb = new StringBuilder();
			quickFill(sb, "Package Name", _parcel.ApplicationPackageName);
			quickFill(sb, "Application Name", _parcel.ApplicationName);
			quickFill(sb, "Version Code", _parcel.ApplicationVersionCode);
			quickFill(sb, "Version Name", _parcel.ApplicationVersionName);
			quickFill(sb, "Base Error", _parcel.ErrorKey);

			try {
				quickFill(sb, "Localized Message", _parcel.ExceptionInfo.getString("localizedmessage"));
				quickFill(sb, "Message", _parcel.ExceptionInfo.getString("message"));
				JSONArray stackTrace = _parcel.ExceptionInfo.getJSONArray("stacktrace");
				StringBuilder st = new StringBuilder();
				final int stLen = stackTrace.length();
				for(int i = 0; i < stLen; i++) {
					String line = stackTrace.getString(i);
					st.append(line);
					st.append("\n");
				}
				quickFill(sb, "Stack Trace", st.toString());
				quickFill(sb, "Localized Message", _parcel.ExceptionInfo.getString("localizedmessage"));
			} catch (Exception e) {
			}


			quickFill(sb, "Application Info", _parcel.ApplicationInfo);
			quickFill(sb, "Device Details", _parcel.DeviceInfo);
			quickFill(sb, "Thread Info", _parcel.ThreadInfo);
			//quickFill(sb, "Exception Info", _parcel.ExceptionInfo);

			return sb.toString();
		}

		private static void quickFill(StringBuilder sb, String title, Object text) {
			sb.append(title);
			sb.append("\n---------------------------\n");
			sb.append(text);
			sb.append("\n\n");
		}

		private static void quickFill(StringBuilder sb, String title, JSONObject contents) {
			sb.append(title);
			sb.append("\n---------------------------\n");
			sb.append(contents);
			//quickFillJson(sb, contents);
			sb.append("\n\n");
		}

		public void deploy() {
			try {
				final NotificationManager nm = (NotificationManager) _context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				PendingIntent pi;
				final Intent intent = new Intent(Intent.ACTION_SEND);
		    	intent.putExtra(Intent.EXTRA_EMAIL, new String[] { emailAddress });
		    	intent.putExtra(Intent.EXTRA_SUBJECT, _parcel.ApplicationName + " crash report.");
		    	intent.putExtra(Intent.EXTRA_TEXT, formatEmailMessage());
		    	intent.setType("message/rfc822");

				pi = PendingIntent.getActivity(_context, (int) System.currentTimeMillis(), intent, 0);

				NotificationCompat.Builder builder = new NotificationCompat.Builder(_context);
				builder.setAutoCancel(true)
					.setSmallIcon(android.R.drawable.stat_notify_error)
					.setContentTitle(_parcel.ApplicationName)
					.setContentText(Html.fromHtml("has crashed <b>click</b> to send bug report."))
					.setTicker(Html.fromHtml(_parcel.ApplicationName + "has crashed <b>click</b> to send bug report."))
					.setContentIntent(pi);
				nm.notify(CrashReporter.NotificationId_DisplayActivity, builder.getNotification());
			} catch (Exception e) {
				Toast.makeText(_context,
						"Crash Report Activity is not registered.",
						Toast.LENGTH_LONG).show();
			}
		}
	}
/*
	static class SilentCrashParachute implements ICrashParachute {
		private final CrashReportParcel _parcel;

		public SilentCrashParachute(CrashReportParcel parcel) {
			_parcel = parcel;
		}

		public void deploy() {
			CrashReportClient client = new CrashReportClient(_parcel);
			Thread t = new Thread(client);
			t.setName("Beryl Crash Reporter");
			t.run();
		}
	}

	static class CrashReportClient implements Runnable {
		final CrashReportParcel _parcel;

		public CrashReportClient(CrashReportParcel parcel) {
			_parcel = parcel;
		}

		public void run() {
			try {
				final DefaultHttpClient client = new DefaultHttpClient();
				HttpParams params = client.getParams();
				HttpConnectionParams.setConnectionTimeout(params, 1500);
				HttpConnectionParams.setSoTimeout(params, 1500);
				client.setParams(params);

				final HttpPost postmessage = new HttpPost(
						_parcel.ServiceEndpointUri);

				// Populate the message
				ArrayList<NameValuePair> nvp = new ArrayList<NameValuePair>();

				nvp.add(new BasicNameValuePair("type", "android"));

				nvp.add(new BasicNameValuePair("UserKey", _parcel.UserKey));
				nvp.add(new BasicNameValuePair("ApplicationId",
						_parcel.ApplicationPackageName));
				nvp.add(new BasicNameValuePair("ErrorKey", _parcel.ErrorKey));
				nvp.add(new BasicNameValuePair("UserComment",
						_parcel.UserComment));

				nvp.add(new BasicNameValuePair("ApplicationInfo",
						_parcel.ApplicationInfo.toString()));
				nvp.add(new BasicNameValuePair("ThreadInfo", _parcel.ThreadInfo
						.toString()));
				nvp.add(new BasicNameValuePair("ExceptionInfo",
						_parcel.ExceptionInfo.toString()));
				nvp.add(new BasicNameValuePair("DeviceInfo", _parcel.DeviceInfo
						.toString()));
				postmessage.setEntity(new UrlEncodedFormEntity(nvp));

				client.execute(postmessage);
			} catch (Exception e) {
				android.util.Log.e("Beryl", "error", e);
			}
		}

	}
*/
	static interface ICrashParachute {
		void deploy();
	}

	static class CrashReportParcel implements Parcelable {
		public String ApplicationName;
		public String ApplicationPackageName;
		public String ApplicationVersionCode;
		public String ApplicationVersionName;
		public String ErrorKey;

		public JSONObject ThreadInfo;
		public JSONObject ExceptionInfo;
		public JSONObject DeviceInfo;
		public JSONObject ApplicationInfo;

		public static final Parcelable.Creator<CrashReportParcel> CREATOR = new Parcelable.Creator<CrashReportParcel>() {
			public CrashReportParcel createFromParcel(Parcel in) {
				return new CrashReportParcel(in);
			}

			public CrashReportParcel[] newArray(int size) {
				return new CrashReportParcel[size];
			}
		};

		@SuppressWarnings("deprecation")
		public CrashReportParcel(final Thread thread, final Throwable ex) {
			ApplicationName = CrashReporter.MetaData_ApplicationName;
			ApplicationPackageName = CrashReporter.MetaData_ApplicationPackageName;
			ApplicationVersionCode = CrashReporter.MetaData_ApplicationVersionCode;
			ApplicationVersionName = CrashReporter.MetaData_ApplicationVersionName;

			try {
				ThreadInfo = new JSONObject();
				ThreadInfo.put("name", thread.getName());
				ThreadInfo.put("state", thread.getState().name());
				ThreadInfo.put("priority", thread.getPriority());

				ExceptionInfo = new JSONObject();

				ExceptionInfo.put("localizedmessage", ex.getLocalizedMessage());
				ExceptionInfo.put("message", ex.getMessage());

				JSONArray jsonstacktrace = new JSONArray();

				addStackTrace(ex, jsonstacktrace);


				ExceptionInfo.put("stacktrace", jsonstacktrace);

				DeviceInfo = new JSONObject();
				DeviceInfo.put("board", Build.BOARD);
				DeviceInfo.put("brand", Build.BRAND);
				DeviceInfo.put("device", Build.DEVICE);
				DeviceInfo.put("model", Build.MODEL);
				DeviceInfo.put("product", Build.PRODUCT);
				DeviceInfo.put("androidrelease", Build.VERSION.RELEASE);
				DeviceInfo.put("apilevel", Build.VERSION.SDK);

				ApplicationInfo = new JSONObject();
				ApplicationInfo.put("name", ApplicationName);
				ApplicationInfo.put("packagename", ApplicationPackageName);
				ApplicationInfo.put("versionname", ApplicationVersionName);
				ApplicationInfo.put("versioncode", ApplicationVersionCode);
			} catch (Exception e) {

			}
		}

		private void addStackTrace(Throwable ex, JSONArray jsonstacktrace) {
			if(ex != null) {
				StackTraceElement[] stacktracedata = ex.getStackTrace();

				jsonstacktrace.put(ex.getClass().getName() + " " + ex.getMessage());
				int i;
				final int len = stacktracedata.length;


				for (i = 0; i < len; i++) {
					if (i == 0) {
						ErrorKey = stacktracedata[i].toString();
					}
					jsonstacktrace.put(stacktracedata[i].toString());
				}
				addStackTrace(ex.getCause(), jsonstacktrace);
			}
		}

		private CrashReportParcel(Parcel in) {
			readFromParcel(in);
		}

		public int describeContents() {
			return 0;
		}

		public void readFromParcel(Parcel in) {
			String buffer;

			ApplicationName = in.readString();
			ApplicationPackageName = in.readString();
			ApplicationVersionCode = in.readString();
			ApplicationVersionName = in.readString();
			ErrorKey = in.readString();

			buffer = in.readString();
			try {
				ThreadInfo = new JSONObject(buffer);
			} catch (Exception e) {
			}

			buffer = in.readString();
			try {
				ExceptionInfo = new JSONObject(buffer);
			} catch (Exception e) {
			}

			buffer = in.readString();
			try {
				DeviceInfo = new JSONObject(buffer);
			} catch (Exception e) {
			}

			buffer = in.readString();
			try {
				ApplicationInfo = new JSONObject(buffer);
			} catch (Exception e) {
			}
		}

		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(ApplicationName);
			dest.writeString(ApplicationPackageName);
			dest.writeString(ApplicationVersionCode);
			dest.writeString(ApplicationVersionName);
			dest.writeString(ErrorKey);

			dest.writeString(ThreadInfo.toString());
			dest.writeString(ExceptionInfo.toString());
			dest.writeString(DeviceInfo.toString());
			dest.writeString(ApplicationInfo.toString());
		}
	}

	public void onThrow(Throwable tr) {
		reportError(tr);
	}

	public void onException(Exception e) {
		reportError(e);
	}
}
