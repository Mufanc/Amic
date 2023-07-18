package xyz.mufanc.amic.module

import picocli.CommandLine.Command
import xyz.mufanc.amic.utils.XposedService

@Command(name = "monitor")
class Monitor {
    @Command(name = "intent")
    fun window() {
        println(XposedService.getService())
    }
}
