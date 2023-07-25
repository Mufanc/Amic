package xyz.mufanc.amic.module

import android.os.Parcel
import android.os.ServiceManager
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import xyz.mufanc.amic.module.service.CommandResultReceiver
import xyz.mufanc.amic.utils.Shell
import java.io.FileDescriptor
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

@Command(name = "service", description = ["System service operations"])
class Service {
    companion object {
        private val PID_TRANSACTION = stringToTransactionCode("_PID")
        private val CMD_TRANSACTION = stringToTransactionCode("_CMD")

        private fun stringToTransactionCode(str: String): Int {
            return str.reversed()
                .mapIndexed { i, ch -> ch.code.shl(i * 8) }
                .sum()
        }

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

        fun command(serviceName: String, args: Array<String>) {
            val service = ServiceManager.getService(serviceName) ?: return
            val req = Parcel.obtain()
            val res = Parcel.obtain()
            try {
                val await = Semaphore(0)
                req.writeFileDescriptor(FileDescriptor.`in`)
                req.writeFileDescriptor(FileDescriptor.out)
                req.writeFileDescriptor(FileDescriptor.err)
                req.writeStringArray(args)
                req.writeStrongBinder(null)  // ShellCallback
                req.writeParcelable(CommandResultReceiver(await), 0)
                service.transact(CMD_TRANSACTION, req, res, 0)
                await.tryAcquire(1, TimeUnit.SECONDS)
            } finally {
                req.recycle()
                res.recycle()
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