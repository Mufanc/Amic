package xyz.mufanc.amic.utils

import java.lang.ref.WeakReference

open class Ref(obj: Any) {

    private val ref = WeakReference(obj)

    open operator fun get(prop: String): Ref? {
        val obj = ref.get() ?: return null
        return Ref(get(obj, prop) ?: return null)
    }

    @Suppress("UNCHECKED_CAST")
    open fun <R> obtain(): R? {
        val obj = ref.get() ?: return null
        return obj as R
    }

    companion object {
        private tailrec fun get(obj: Any, prop: String): Any? {
            val clazz = obj.javaClass
            var current: Class<*>? = clazz

            var err: Exception? = null

            while (current != null) {
                try {
                    return current.getDeclaredField(prop).apply { isAccessible = true }.get(obj)
                } catch (exception: NoSuchFieldException) {
                    if (err == null) err = exception
                    current = current.superclass
                }
            }

            current = clazz.enclosingClass ?: throw err!!
            val enclosing = clazz.declaredFields
                .find { it.type == current && it.name.startsWith("this$") }!!
                .apply { isAccessible = true }
                .get(obj)!!

            return get(enclosing, prop)
        }
    }
}