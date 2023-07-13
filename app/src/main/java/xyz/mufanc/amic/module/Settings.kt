package xyz.mufanc.amic.module

import android.os.FileObserver
import android.os.Process
import android.util.ArrayMap
import android.util.XmlHidden
import org.xmlpull.v1.XmlPullParser
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import xyz.mufanc.amic.utils.Common
import java.io.File

@Command(name = "settings")
object Settings {

    enum class Namespace {
        ALL, GLOBAL, SYSTEM, SECURE;
        fun id() = name.lowercase()
    }

    private lateinit var observer: SettingsObserver

    private val namespaces = Namespace.values()
        .mapNotNull { it.takeIf { it != Namespace.ALL }?.id() }
        .toSet()

    private val pattern = "settings_(${namespaces.joinToString("|")}).xml".toRegex()

    data class Result<K, T>(
        val n: List<T>,  // new
        val r: List<T>,  // removed
        // key, (old, new)
        val m: Map<K, Pair<T, T>>
    )

    interface Diff<K, T> {
        fun keys(): Set<K>
        fun get(key: K): T
        fun dump(keys: Iterable<K>): List<T>

        fun compare(other: Diff<K, T>): Result<K, T>? {
            val tk = keys()
            val rk = other.keys()
            val ck = tk.intersect(rk)

            val n = other.dump(rk - tk)
            val r = dump(tk - rk)
            val m = ck.mapNotNull { k ->
                val a = other.get(k)  // after
                val b = get(k)  // before
                Pair(k, Pair(b, a)).takeIf { a != b }
            }.toMap()

            if (n.isEmpty() && r.isEmpty() && m.isEmpty()) return null
            return Result(n, r, m)
        }
    }

    data class SettingsItem(val name: String, val `package`: String, val value: String) {
        companion object {
            fun tryFrom(attrs: ArrayMap<String, String>): SettingsItem? {
                if (!(attrs.containsKey("id") && attrs.containsKey("name") && attrs.containsKey("package") && attrs.containsKey("value"))) {
                    return null
                }
                return SettingsItem(attrs["name"]!!, attrs["package"]!!, attrs["value"]!!)
            }
        }
    }

    data class SettingsForm(val data: Map<String, SettingsItem>) : Diff<String, SettingsItem> {
        override fun keys(): Set<String> = data.keys
        override fun get(key: String): SettingsItem = data[key]!!
        override fun dump(keys: Iterable<String>): List<SettingsItem> = keys.map { data[it]!! }

        companion object {
            fun of(namespace: Namespace): SettingsForm {
                val data = mutableMapOf<String, SettingsItem>()
                val parser = XmlHidden.newBinaryPullParser()
                parser.setInput(File(getWatchDir(), "settings_${namespace.id()}.xml").inputStream(), Charsets.UTF_8.name())

                var eventType: Int
                do {
                    eventType = parser.next()
                    when (eventType) {
                        XmlPullParser.START_TAG -> {
                            if (parser.name != "setting") continue
                            val attrs = ArrayMap<String, String>()
                            var index = parser.attributeCount - 1
                            while (index >= 0) {
                                attrs[parser.getAttributeName(index)] = parser.getAttributeValue(index)
                                index -= 1
                            }
                            val name = attrs["name"]!!
                            data[name] = SettingsItem.tryFrom(attrs) ?: continue
                        }
                    }
                } while (eventType != XmlPullParser.END_DOCUMENT)

                return SettingsForm(data)
            }
        }
    }

    private val cache by lazy {
        ArrayMap<Namespace, SettingsForm>().apply {
            put(Namespace.GLOBAL, SettingsForm.of(Namespace.GLOBAL))
            put(Namespace.SYSTEM, SettingsForm.of(Namespace.SYSTEM))
            put(Namespace.SECURE, SettingsForm.of(Namespace.SECURE))
        }
    }

    private fun getWatchDir(): File {
        return File("/data/system/users/${Process.myUserHandle().hashCode()}")
    }

    private fun println(color: String, message: String) {
        val isatty = System.console() != null
        if (isatty) print("\u001b[${color}m")
        print(message)
        if (isatty) print("\u001b[0m")
        println()
    }

    private class SettingsObserver(private val nsTarget: Namespace) : FileObserver(getWatchDir(), MOVED_TO or MODIFY) {
        override fun onEvent(event: Int, path: String?) {
            try {
                if (path == null) return

                val ns = (pattern.find(path) ?: return).groupValues[1]
                val namespace = Namespace.valueOf(ns.uppercase())

                if (nsTarget != Namespace.ALL && namespace != nsTarget) return

                val after = SettingsForm.of(namespace)
                val changes = cache[namespace]!!.compare(after) ?: return

                changes.n.forEach { item ->
                    println("32", "+ [$ns] ${item.name}: ${item.value}")
                }
                changes.r.forEach { item ->
                    println("31", "- [$ns] ${item.name} (${item.value})")
                }
                changes.m.forEach { (name, pair) ->
                    val (pre, now) = pair
                    println("0", "* [$ns] $name: ${pre.value} -> ${now.value}")
                }

                cache[namespace] = after
            } catch (err: Throwable) {
                err.printStackTrace()
            }
        }

        fun start() = startWatching()
    }

    @Command(name = "watch", description = [ "Watch for settings changes" ])
    fun watch(
        @Parameters(paramLabel = "[namespace]", defaultValue = "ALL", description = [ "GLOBAL, SYSTEM, SECURE, or default to ALL" ])
        namespace: Namespace
    ) {
        if (!Common.checkPermission()) return

        observer = SettingsObserver(namespace).apply { start() }

        System.`in`.readBytes()  // yield
    }
}