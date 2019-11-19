package org.zodiac.commonutils.text;

public class PathUtil {

    public static String getParent(String path) {
        return new Path(path).parent().toString();
    }

    public static String join(String path, String... paths) {
        return new Path(path).join(paths).toString();
    }
}
