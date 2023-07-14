MODDIR=${0%/*}
RANDOM_DIR="/dev/amic-$(base64 /dev/urandom | grep -Eo '[^+/]{3}' | head -n 1)"
APK_PATH="$RANDOM_DIR/amic.apk"
CONTEXT="u:object_r:shell_data_file:s0"

mkdir -p "$RANDOM_DIR"
cp "$MODDIR/amic.apk" "$APK_PATH"
chmod 775 "$RANDOM_DIR"
chmod 664 "$APK_PATH"
chcon "$CONTEXT" "$RANDOM_DIR"
chcon "$CONTEXT" "$APK_PATH"
