package xyz.mufanc.amic

import picocli.CommandLine
import picocli.CommandLine.Command
import xyz.mufanc.amic.module.Abx
import xyz.mufanc.amic.module.FindClass
import xyz.mufanc.amic.module.Service
import kotlin.system.exitProcess

@Command(
    name = "amic",
    description = [ "Android Management Instrumentation Commands" ],
    subcommands = [
        Service::class,
        FindClass::class,
        Abx::class
    ],
    mixinStandardHelpOptions = true,
)
class Main {
    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            try {
                val code = CommandLine(Main()).execute(*args)
                exitProcess(code)
            } catch (exception: Exception) {
                exception.printStackTrace(System.err)
            }
        }
    }
}
