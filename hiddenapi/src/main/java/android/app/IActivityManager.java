package android.app;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;

public interface IActivityManager extends IInterface {
    void setActivityController(IActivityController controller, boolean imAMonkey);

    abstract class Stub extends Binder implements IActivityManager {
        public static IActivityManager asInterface(IBinder obj) {
            throw new RuntimeException("Stub!");
        }
    }
}
