set -aux

./gradlew :app:assembleRelease

adb push app/build/outputs/apk/release/app-release-unsigned.ash /data/local/tmp/amic
adb shell chmod +x /data/local/tmp/amic
adb shell /data/local/tmp/amic "$@"
