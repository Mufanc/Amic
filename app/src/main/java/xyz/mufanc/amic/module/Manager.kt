package xyz.mufanc.amic.module

import picocli.CommandLine.Command
import xyz.mufanc.amic.utils.Common
import xyz.mufanc.amic.utils.Shell

@Command(name = "manager")
object Manager {
    @Command(name = "install")
    fun installApk() {
        // Todo: use PackageInstaller api
//        Shell.exec("/system/bin/pm install ${Common.currentApk()}")
        Service.command("package", arrayOf("install", Common.currentApk().absolutePath))
    }
}
