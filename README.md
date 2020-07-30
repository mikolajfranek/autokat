# autokat

#### run android studio
"sudo /home/ubuntu/android-studio/bin/studio.sh"

#### run emulator
"/root/Android/Sdk/emulator/emulator -avd api29"

#### run emulator (as root)
"/root/Android/Sdk/emulator/emulator -avd api29 -writable-system -selinux disabled"

#### create file *.jks for deploy app
"keytool -genkey -keyalg RSA -keystore keystore.jks -keysize 2048 -alias autokat"

#### pull data from emulator to computer 
"adb pull 'remote' 'local'"

#### short instruction how to be root in emulator
1. run emulator with options "-writable-system -selinux disabled"
2. "adb root && adb remount"
3. "unzip root_avd-master.zip"
4. "adb install ./root_avd/SuperSU/common/Superuser.apk"
5. "adb push ./root_avd/SuperSU/x86/su /system/xbin/su"
6. "adb shell chmod 0755 /system/xbin/su"
7. "adb shell setenforce 0"
8. "adb shell su --install"
9. "adb shell su --daemon&"
10. open SuperSU in emulator 
11. root is activated, in problems type "su --daemon&"
