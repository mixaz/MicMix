Example code to write a file to system partition, works only on rooted phones (with SU).

The app was written for a friend of mine, who needed to switch some parameters in /system/etc/mixer_paths.xml

The app screen has following buttons:

1. remount system part to RW
2. copy a template file from assets folder to /system/etc, replacing %DEC_VOL% to values provided in text field
3. reboot phone

Tested on Genymotion emulator Android 5.1
