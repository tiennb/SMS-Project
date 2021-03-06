package net.everythingandroid.smspopup.util;

public class Log {
    public final static String LOGTAG = "SMSPopup";

    public static void v(String msg) {
        android.util.Log.v(LOGTAG, msg);
    }

    public static void e(String msg) {
        android.util.Log.e(LOGTAG, msg);
    }
}
