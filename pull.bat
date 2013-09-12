@echo off

call "C:\Users\Robert Prouse\Documents\Batch\Android.bat"

adb root
adb pull /data/data/net.alteridem.mileage/databases/mileage.db backup/mileage.db