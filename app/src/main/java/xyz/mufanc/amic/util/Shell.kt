package xyz.mufanc.amic.util

object Shell {
    fun exec(command: String) {
        val proc = ProcessBuilder("/system/bin/sh", "-c", command)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
        proc.waitFor()
    }
}
