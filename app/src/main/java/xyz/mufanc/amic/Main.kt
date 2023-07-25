package xyz.mufanc.amic

import android.os.ProcessHidden
import picocli.CommandLine
import picocli.CommandLine.Command
import xyz.mufanc.amic.module.Abx
import xyz.mufanc.amic.module.FindClass
import xyz.mufanc.amic.module.Manager
import xyz.mufanc.amic.module.Monitor
import xyz.mufanc.amic.module.Service
import xyz.mufanc.amic.module.Settings
import kotlin.system.exitProcess

@Command(
    name = "amic",
    description = [ "Android Management Instrumentation Commands" ],
    subcommands = [
        Service::class,
        FindClass::class,
        Abx::class,
        Settings::class,
        Monitor::class,
        Manager::class,
    ],
    mixinStandardHelpOptions = true,
)
object Main {
    @JvmStatic
    fun main(vararg args: String) {
        try {
            ProcessHidden.setArgV0("amic")
            CommandLine(this).apply {
                isCaseInsensitiveEnumValuesAllowed = true
                exitProcess(execute(*args))
            }
        } catch (exception: Exception) {
            exception.printStackTrace(System.err)
        }
    }
}
