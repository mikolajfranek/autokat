# autokat

#### run android studio
sudo /home/ubuntu/android-studio/bin/studio.sh

#### run emulator
/root/Android/Sdk/emulator/emulator -avd api29

#### run emulator (as root)
/root/Android/Sdk/emulator/emulator -avd api29 -writable-system -selinux disabled

#### create file *.jks for deploy app
keytool -genkey -keyalg RSA -keystore keystore.jks -keysize 2048 -alias autokat

#### pull data from emulator to computer 
adb pull 'remote' 'local'

#### short instruction how to be root in emulator
run emulator with options 
-writable-system -selinux disabled

$ adb root && adb remount

unzip root_avd-master.zip

$ adb install ./root_avd/SuperSU/common/Superuser.apk 

$ adb push ./root_avd/SuperSU/x86/su /system/xbin/su

$ adb shell chmod 0755 /system/xbin/su

$ adb shell setenforce 0

$ adb shell su --install

$ adb shell su --daemon&

open SuperSU in emulator 

root is activated, in problems type
$ su --daemon&