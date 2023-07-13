package xyz.mufanc.amic.utils

import android.os.Process
import java.io.File

object Common {
    fun currentApk(): File {
        val dexArray: Array<*> = Ref(javaClass.classLoader!!)["pathList"]!!["dexElements"]!!.obtain()!!
        return Ref(dexArray[0]!!)["path"]!!.obtain()!!
    }

    fun checkPermission(): Boolean {
        if (Process.myUid() != 0) {
            System.err.println("require root privilege!")
            return false
        }
        return true
    }
}