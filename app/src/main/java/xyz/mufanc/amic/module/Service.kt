package xyz.mufanc.amic.module

import android.os.Parcel
import android.os.ServiceManager
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import xyz.mufanc.amic.common.Shell

@Command(name = "service", description = ["System service operations"])
class Service {
    companion object {
        val PID_TRANSACTION = "_PID"
            .reversed()
            .mapIndexed { i, ch -> ch.code.shl(i * 8) }
            .sum()
    }

    @Command(name = "pid", description = [ "Get the pid of a specific service" ])
    fun pid(
        @Parameters(paramLabel = "<name>", description = [ "service name" ])
        name: String
    ) {
        val service = ServiceManager.getService(name)
        if (service != null) {
            val req = Parcel.obtain()
            val res = Parcel.obtain()
            try {
                service.transact(PID_TRANSACTION, req, res, 0)
                val pid = res.readInt()
                println("Pid of `$name` service is $pid:")
                Shell.exec("ps -p $pid")
            } finally {
                req.recycle()
                res.recycle()
            }
        } else {
            println("Service named `$name` not found!")
        }
    }
}