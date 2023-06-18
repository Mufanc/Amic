package xyz.mufanc.amic.modules

import xyz.mufanc.amic.ArgParser

object Cli : ArgParser<Cli>() {
    @SubCommand("service")
    fun service() = Service
}
