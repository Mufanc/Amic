package android.util;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import dev.rikka.tools.refine.RefineAs;

@RefineAs(Xml.class)
public class XmlHidden {
    public static TypedXmlPullParser newBinaryPullParser() {
        throw new RuntimeException("STUB");
    }

    public static TypedXmlSerializer newBinarySerializer() {
        throw new RuntimeException("STUB");
    }

    public static void copy(XmlPullParser in, XmlSerializer out) {
        throw new RuntimeException("STUB");
    }
}
