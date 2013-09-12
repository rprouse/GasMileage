@echo off

call "C:\Users\Robert Prouse\Documents\Batch\Android.bat"

adb root
adb push backup/mileage.db /data/data/net.alteridem.mileage/databases/mileage.db