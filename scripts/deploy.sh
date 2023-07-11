set -eux

DEPLOY_PATH="/data/local/tmp/amic.apk"

./gradlew :app:aRelease

adb push app/build/outputs/apk/release/*-release-*.apk "$DEPLOY_PATH"

adb shell "CLASSPATH=$DEPLOY_PATH app_process / xyz.mufanc.amic.Main $*"
