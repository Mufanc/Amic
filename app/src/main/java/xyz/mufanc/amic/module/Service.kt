package xyz.mufanc.amic.module

import android.os.Parcel
import android.os.ServiceManager
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import xyz.mufanc.amic.utils.Shell

@Command(name = "service", description = ["System service operations"])
class Service {
    companion object {
        private val PID_TRANSACTION = "_PID"
            .reversed()
            .mapIndexed { i, ch -> ch.code.shl(i * 8) }
            .sum()

        fun getServicePid(name: String): Int? {
            val service = ServiceManager.getService(name) ?: return null
            val req = Parcel.obtain()
            val res = Parcel.obtain()
            try {
                service.transact(PID_TRANSACTION, req, res, 0)
                return res.readInt()
            } finally {
                req.recycle()
                req.recycle()
            }
        }
    }

    @Command(name = "pid", description = [ "Get the pid of a specific service" ])
    fun pid(
        @Parameters(paramLabel = "<name>", description = [ "service name" ])
        name: String
    ) {
        val pid = getServicePid(name)
        if (pid != null) {
            println("pid of `$name` service is $pid:")
            Shell.exec("ps -p $pid")
        } else {
            println("service named `$name` not found!")
        }
    }
}