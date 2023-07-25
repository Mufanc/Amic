package xyz.mufanc.amic.module

import android.app.IActivityController
import android.app.IActivityManager
import android.content.Context
import android.content.Intent
import android.os.ServiceManager
import picocli.CommandLine.Command
import java.lang.StringBuilder

@Command(name = "monitor")
class Monitor : Runnable {

    override fun run() {
        // Todo：尝试拿到完整信息
        val iam = IActivityManager.Stub.asInterface(ServiceManager.getService(Context.ACTIVITY_SERVICE))
        val monitor = ActivityMonitor()
        iam.setActivityController(monitor, false)
        System.`in`.readBytes()  // yield
    }

    class ActivityMonitor : IActivityController.Stub() {
        override fun activityStarting(intent: Intent?, pkg: String?): Boolean {
            if (intent != null) {
                val info = Indented(" ".repeat(4)) {
                    block("Intent") {
                        intent.action?.let { action ->
                            println("action: $action")
                        }

                        intent.type?.let { type ->
                            println("type: $type")
                        }

                        intent.component?.let { component ->
                            println("component: ${component.flattenToShortString()}")
                        }

                        intent.categories?.let{ categories ->
                            println("categories: [ ${categories.joinToString(", ")} ]")
                        }

                        intent.data?.let { data ->
                            println("data: $data")
                        }

                        if (intent.flags != 0) {
                            println("flags: ${intent.flags}")
                        }

                        intent.extras?.let { extras ->
                            println("extras: $extras")
                        }
                    }
                }
                println(info)
            }
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

    class Indented(private val prefix: String, func: Indented.() -> Unit) {

        private var indent = 0
        private val builder = StringBuilder()

        fun block(name: String, func: Indented.() -> Unit) {
            println("$name {")
            indent += 1
            func(this)
            indent -= 1
            println("}")
        }

        fun <T> append(obj: T): Indented {
            builder.append(obj)
            return this
        }

        fun <T> print(obj: T): Indented {
            append(prefix.repeat(indent)).append(obj)
            return this
        }

        fun <T> println(obj : T): Indented {
            print(obj).append("\n")
            return this
        }

        override fun toString(): String {
            return builder.toString()
        }

        init {
            func(this)
        }
    }
}
