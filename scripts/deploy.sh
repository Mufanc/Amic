set -eux
./gradlew :app:aRelease
adb push app/build/outputs/apk/release/*-release-*.apk /data/local/tmp/amic.apk
adb shell app_process -Djava.class.path=/data/local/tmp/amic.apk / xyz.mufanc.amic.Main "$@"

