package xyz.mufanc.amic

import java.lang.annotation.AnnotationFormatError
import java.lang.reflect.ParameterizedType
import java.util.LinkedList
import java.util.Queue

abstract class ArgParser<T> {
    fun parse(args: Array<out String>) = parse(LinkedList(args.toMutableList()))

    @Suppress("UNCHECKED_CAST")
    private fun parse(args: Queue<String>) {
        val klass = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>

        for (handler in klass.declaredMethods) {
            val annotations = handler.declaredAnnotations.filter {
                it.annotationClass.java.getDeclaredAnnotation(Option::class.java) != null
            }

            if (annotations.isEmpty()) continue
            if (annotations.size >= 2) throw AnnotationFormatError("Multiple option types specified for method!")

            val annotation = annotations[0]
            when {
                !args.isEmpty() && annotation is SubCommand && args.peek() == annotation.name -> {
                    assert(handler.parameterCount == 0 && handler.returnType.isAssignableFrom(ArgParser::class.java))
                    args.remove()
                    val subcommand = handler.invoke(this) as ArgParser<*>
                    subcommand.parse(args)
                    return
                }
            }
        }

        val remains = mutableListOf<String>()
        while (true) {
            remains.add(args.poll() ?: break)
        }

        onArgs(remains)
    }

    open fun onArgs(args: List<String>) = Unit

    private annotation class Option

    @Option
    @Target(AnnotationTarget.FUNCTION)
    annotation class SubCommand(val name: String)
}
