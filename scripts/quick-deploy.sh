set -eu

./gradlew :app:aDebug

TMP_FILE="/data/local/tmp/amic.apk"
DEPLOY_PATH="/dev/$(adb shell ls /dev | grep 'amic-*')/amic.apk"

adb push app/build/outputs/apk/debug/app-debug.apk "$TMP_FILE"
adb shell su -c "cp $TMP_FILE $DEPLOY_PATH"
adb shell "CLASSPATH=$DEPLOY_PATH app_process / xyz.mufanc.amic.Main $*"
