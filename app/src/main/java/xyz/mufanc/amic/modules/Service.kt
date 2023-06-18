package xyz.mufanc.amic.modules

import android.os.Parcel
import android.os.ServiceManager
import xyz.mufanc.amic.ArgParser
import xyz.mufanc.amic.util.Shell

object Service : ArgParser<Service>() {
    @SubCommand("pid")
    fun pid() = PidCommand()

    class PidCommand : ArgParser<PidCommand>() {
        companion object {
            val TRANSACTION_CODE = "_PID"
                .reversed()
                .toByteArray()
                .mapIndexed { i, ch -> ch.toInt() shl (i * 8) }
                .sum()
        }

        override fun onArgs(args: List<String>) {
            val name = args[0]
            val service = ServiceManager.getService(name)

            if (service != null) {
                val req = Parcel.obtain()
                val res = Parcel.obtain()
                try {
                    service.transact(TRANSACTION_CODE, req, res, 0)
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
}
