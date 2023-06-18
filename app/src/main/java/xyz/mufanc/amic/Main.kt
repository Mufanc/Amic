package xyz.mufanc.amic

import xyz.mufanc.amic.modules.Cli

object Main {
    @JvmStatic
    fun main(vararg args: String) {
        try {
            Cli.parse(args)
        } catch (err: Throwable) {
            err.printStackTrace(System.err)
        }
    }
}
