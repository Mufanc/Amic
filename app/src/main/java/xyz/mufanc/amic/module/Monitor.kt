package xyz.mufanc.amic.module

import picocli.CommandLine.Command
import xyz.mufanc.amic.utils.XposedService
import xyz.mufanc.amic.utils.eprintln

@Command(name = "monitor")
class Monitor {
    @Command(name = "intent")
    fun intent() {
        // Todo: command: install apk
        val ixs = XposedService.getService()
        if (ixs == null) {
            eprintln("Failed to fetch xposed service!")
            return
        }
        if (!XposedService.isVersionCompatible(ixs)) return
        println("success")
    }
}
