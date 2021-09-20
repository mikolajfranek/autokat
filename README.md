# autokat
Catalyst app

#### run android studio
"/home/debian/android-studio-2021.1.1.12-linux/android-studio/bin/studio.sh"

#### older running emulator
"/home/debian/Android/Sdk/emulator/emulator -avd api27"

#### older running emulator (as root)
"/home/debian/Android/Sdk/emulator/emulator -avd api27 -writable-system -qemu -enable-kvm" 

#### create file *.jks for deploy app
"keytool -genkey -keyalg RSA -keystore keystore.jks -keysize 2048 -alias autokat"

#### pull data from emulator to computer 
"adb pull 'remote' 'local'"

#### short instruction how to be root in emulator
1. run emulator "as root"
2. "unzip root_avd-master.zip"
3. "adb root"
4. "adb remount"
5. "adb install ./root_avd-master/SuperSU/common/Superuser.apk"
6. "adb push ./root_avd-master/SuperSU/x86/su /system/xbin/su"
7. "adb shell chmod 0755 /system/xbin/su"
8. "adb shell setenforce 0"
9. "adb shell su --install"
10. "adb shell su --daemon&"
11. open SuperSU, installation will be failed, but root will be activated
 

