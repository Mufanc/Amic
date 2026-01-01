package xyz.mufanc.amic.applets

import android.app.INotificationManager
import android.os.Binder
import android.os.ServiceManager
import android.system.Os
import android.widget.Toast
import picocli.CommandLine

@CommandLine.Command(name = "toast")
class SimpleToast : Runnable {

    @CommandLine.Parameters(paramLabel = "<message>")
    private lateinit var message: String

    override fun run() {
        val inm = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"))

        if (Os.geteuid() == 0) {
            @Suppress("DEPRECATION")
            Os.seteuid(1000)
            inm.enqueueTextToast("android", Binder(), message, Toast.LENGTH_SHORT, false, 0, null)
        } else {
            inm.enqueueTextToast("com.android.shell", Binder(), message, Toast.LENGTH_SHORT, false, 0, null)
        }
    }
}
