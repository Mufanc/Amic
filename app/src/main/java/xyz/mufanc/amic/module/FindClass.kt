package xyz.mufanc.amic.module

import android.content.Context
import android.system.Os
import dalvik.system.PathClassLoader
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters
import xyz.mufanc.amic.utils.Common
import java.io.File

@Command(name = "findclass", description = [ "Find all .jar/.apk file that contains specific class" ])
class FindClass : Runnable {

    @Option(names = [ "-v", "--visual" ])
    private var visual: Boolean = false

    @Option(names = [ "--apex" ], description = [ "resolve packages in /apex that mapped by system_server" ])
    private var resolveApexPackage: Boolean = false

    @Parameters(paramLabel = "<classname>")
    private lateinit var name: String

    override fun run() {
        val pid = Service.getServicePid(Context.ACTIVITY_SERVICE)!!

        val searchList = getProcessClasspath().toMutableSet()

        if (resolveApexPackage) {
            if (!Common.checkPermission()) return
            searchList.addAll(getProcessMappedJavaLibs(pid))
        }

        searchList.remove(Common.currentApk().path)

        val count = searchList.count { jar ->
            val loader = PathClassLoader(jar, null)

            try {
                loader.loadClass(name)
                println("$jar -> $name")
                return@count true
            } catch (_: ClassNotFoundException) {
                if (visual) {
                    println("$jar -> Not Found!")
                }
            }

            false
        }

        if (count == 0) {
            println("class `${name}` not found!")
        }
    }

    private fun getProcessClasspath(): List<String> {
        return Os.environ()
            .mapNotNull {  kv ->
                val (key, value) = kv.split('=')
                return@mapNotNull if (key.endsWith("CLASSPATH")) value.split(':') else null
            }
            .flatten()
    }

    private fun getProcessMappedJavaLibs(pid: Int): List<String> {
        return File("/proc/$pid/maps").readLines()
            .mapNotNull { mapinfo ->
                val filepath = mapinfo.split("\\s+".toRegex())[5]
                if (filepath.matches("/apex/[^/]+/javalib/[^/]+\\.jar".toRegex())) {
                    return@mapNotNull filepath
                }
                if (filepath.matches("/apex/[^/]+/priv-app/[^/]+/[^/]+\\.apk".toRegex())) {
                    return@mapNotNull filepath
                }
                null
            }
    }
}