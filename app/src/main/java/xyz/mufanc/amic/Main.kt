package xyz.mufanc.amic

import android.annotation.SuppressLint
import dalvik.system.BaseDexClassLoader
import java.io.File

fun catch(block: () -> Unit) {
    try {
        block()
    } catch (err: Throwable) {
        error(err)
    }
}

@SuppressLint("DiscouragedPrivateApi")
object Main {
    init {
        BaseDexClassLoader::class.java.getDeclaredField("pathList")
            .apply { isAccessible = true }
            .get(javaClass.classLoader)
            .let { pathList ->
                pathList.javaClass.getDeclaredField("dexElements")
                    .apply { isAccessible = true }
                    .get(pathList)
                    .let { it as Array<*> }
                    .first()!!
                    .let { element ->
                        element.javaClass.getDeclaredField("path")
                            .apply { isAccessible = true }
                            .get(element)
                    }
            }
            .let { it as File }
            .takeIf { it.path == "/data/local/tmp/.amic.apk" }
            ?.delete()
    }

    @JvmStatic
    fun main(vararg args: String) {
        catch {
            println("Hello: ${args.contentToString()}")
        }
    }
}
