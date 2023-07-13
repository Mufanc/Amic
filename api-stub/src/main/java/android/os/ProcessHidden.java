package android.os;

import dev.rikka.tools.refine.RefineAs;

@RefineAs(Process.class)
public class ProcessHidden {
    public static void setArgV0(String argV0) {
        throw new RuntimeException("STUB");
    }
}
