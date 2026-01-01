package xyz.mufanc.amic.applets

import android.app.IActivityController
import android.app.IActivityManager
import android.content.Intent
import android.os.ServiceManager
import picocli.CommandLine
import xyz.mufanc.amic.misc.StructureBuilder
import java.io.BufferedReader
import java.io.InputStreamReader

@CommandLine.Command(name = "monitor")
class ActivityMonitor : Runnable {

    override fun run() {
        val iam = IActivityManager.Stub.asInterface(ServiceManager.getService("activity"))
        val monitor = ActivityMonitor()

        iam.setActivityController(monitor, false)

        val reader = BufferedReader(InputStreamReader(System.`in`))
        reader.use { it.readLine() }

        iam.setActivityController(null, false)
    }

    class ActivityMonitor : IActivityController.Stub() {
        override fun activityStarting(intent: Intent?, pkg: String?): Boolean {
            do {
                if (intent == null || pkg == null) {
                    break
                }

                val builder = StructureBuilder()
                val message = builder.build {
                    append("Package: $pkg\n")
                    block("Intent") {
                        append("action: ${intent.action}\n")
                        append("type: ${intent.type}\n")
                        append("data: ${intent.data}\n")
                        append("component: ${intent.component}\n")
                        append("categories: ${intent.categories}\n")
                        append("flags: ${intent.flags}\n")
                    }
                }

                println(message)
            } while (false)

            return true
        }

        override fun activityResuming(pkg: String?): Boolean {
            return true
        }

        override fun appCrashed(processName: String?, pid: Int, shortMsg: String?, longMsg: String?, timeMillis: Long, stackTrace: String?): Boolean {
            return true
        }

        override fun appEarlyNotResponding(processName: String?, pid: Int, annotation: String?): Int {
            return 0
        }

        override fun appNotResponding(processName: String?, pid: Int, processStats: String?): Int {
            return 0
        }

        override fun systemNotResponding(msg: String?): Int {
            return -1
        }
    }
}
