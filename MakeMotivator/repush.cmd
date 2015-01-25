@echo off
echo Uninstalling
adb uninstall com.futonredemption.makemotivator

echo Installing
adb install MakeMotivator.apk
echo Running
adb shell am start -a android.intent.action.MAIN -n com.futonredemption.makemotivator/.activities.MainActivity