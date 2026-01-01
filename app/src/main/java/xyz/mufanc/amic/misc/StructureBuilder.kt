package xyz.mufanc.amic.misc

class StructureBuilder(
    private val indent: String = " ".repeat(4)
) {

    private val nodes = ArrayList<Node>()

    fun build(block: StructureBuilder.() -> Unit): String {
        block(this)
        return toString()
    }

    fun block(name: String = "", brackets: Pair<String, String> = Pair("{", "}"), block: StructureBuilder.() -> Unit) {
        nodes.add(Node.Text("$name ${brackets.first}\n"))
        nodes.add(Node.Indent(1))
        block()
        nodes.add(Node.Indent(-1))
        nodes.add(Node.Text("${brackets.second}\n"))

    }

    fun append(arg: Any?): StructureBuilder {
        when (arg) {
            is Array<*> -> {
                nodes.add(Node.Text(arg.contentDeepToString()))
            }
            else -> {
                nodes.add(Node.Text(arg.toString()))
            }
        }

        return this
    }

    override fun toString(): String {
        val builder = StringBuilder()
        var level = 0

        for (node in nodes) {
            when (node) {
                is Node.Indent -> {
                    level += node.level
                }
                is Node.Text -> {
                    builder.append(indent.repeat(level))
                    builder.append(node.text)
                }
             }
        }

        return builder.toString()
    }

    private sealed interface Node {
        class Indent(val level: Int) : Node
        class Text(val text: String) : Node
    }
}