package com.futonredemption.makemotivator;

import android.annotation.TargetApi;
import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

@TargetApi(8)
public class ApplicationBackupAgent extends BackupAgentHelper {

	@Override
    public void onCreate() {
		super.onCreate();
		final SharedPreferencesBackupHelper backupHelper = new SharedPreferencesBackupHelper(this, Constants.Preferences.ApplicationKey);
		addHelper(Constants.Preferences.BackupAgentKey, backupHelper);
		// http://developer.android.com/guide/topics/data/backup.html#BackupAgentHelper
	}
}
