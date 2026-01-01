package android.app;

import android.os.Binder;
import android.os.IBinder;

public interface INotificationManager {

    boolean enqueueTextToast(String pkg, IBinder token, CharSequence text, int duration, boolean isUiContext, int displayId, ITransientNotificationCallback callback);

    abstract class Stub extends Binder implements INotificationManager {
        public static INotificationManager asInterface(IBinder obj) {
            throw new RuntimeException("Stub!");
        }
    }
}
