package xyz.mufanc.amic

import android.os.ProcessHidden
import kotlinx.coroutines.Runnable
import picocli.CommandLine
import xyz.mufanc.amic.applets.ActivityMonitor
import xyz.mufanc.amic.applets.SimpleToast
import xyz.mufanc.aproc.annotation.AProcEntry
import kotlin.system.exitProcess

@CommandLine.Command(
    name = "amic",
    description = [ "Android Management Instrumentation Commands" ],
    subcommands = [
        ActivityMonitor::class,
        SimpleToast::class
    ],
    mixinStandardHelpOptions = true
)
@AProcEntry
object Main : Runnable {

    @JvmStatic
    fun main(vararg args: String) {
        try {
            ProcessHidden.setArgV0("amic")
            CommandLine(this).apply {
                isCaseInsensitiveEnumValuesAllowed = true
                exitProcess(execute(*args))
            }
        } catch (err: Throwable) {
            err.printStackTrace(System.err)
        }
    }

    override fun run() {
        CommandLine.usage(this, System.out)
    }
}
