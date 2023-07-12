package xyz.mufanc.amic.utils

import java.io.File

object Common {
    fun currentApk(): File {
        val dexArray: Array<*> = Ref(javaClass.classLoader!!)["pathList"]!!["dexElements"]!!.obtain()!!
        return Ref(dexArray[0]!!)["path"]!!.obtain()!!
    }
}