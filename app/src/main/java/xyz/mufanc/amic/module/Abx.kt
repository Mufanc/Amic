package xyz.mufanc.amic.module

import android.util.Xml
import android.util.XmlHidden
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlSerializer
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.io.File
import java.nio.charset.StandardCharsets

@Command(name = "abx", description = [ "Android binary XML utilities" ])
class Abx {
    @Command(name = "decode", aliases = [ "d" ], description = [ "Convert Android Binary XML to human-readable XML" ])
    fun decode(
        @Parameters(paramLabel = "<input>", description = [ "binary XML file, or `-` to stdin" ])
        abxFile: File
    ) {
        val inputStream = if (abxFile.path == "-") System.`in` else abxFile.inputStream()
        val src = XmlHidden.newBinaryPullParser()
        val dst = Xml.newSerializer()
        src.setInput(inputStream, StandardCharsets.UTF_8.name())
        dst.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true)
        dst.setOutput(System.out, StandardCharsets.UTF_8.name())
        pipe(src, dst)
    }

    @Command(name = "encode", aliases = [ "e" ], description = [ "Convert human-readable XML to Android Binary XML" ])
    fun encode(
        @Parameters(paramLabel = "<input>", description = [ "human-readable XML file, or `-` to stdin" ])
        xmlFile: File,
        @Parameters(paramLabel = "[output]", defaultValue = "-", description = [ "output file, or `-` to stdout" ])
        outFile: File = File("-")
    ) {
        val inputStream = if (xmlFile.path == "-") System.`in` else xmlFile.inputStream()
        val outputStream = if (outFile.path == "-") System.out else outFile.outputStream()
        val src = Xml.newPullParser()
        val dst = XmlHidden.newBinarySerializer()
        src.setInput(inputStream, StandardCharsets.UTF_8.name())
        dst.setOutput(outputStream, StandardCharsets.UTF_8.name())
        pipe(src, dst)
    }

    private fun pipe(src: XmlPullParser, dst: XmlSerializer) {
        XmlHidden.copy(src, dst)
        dst.flush()
        println()
    }
}