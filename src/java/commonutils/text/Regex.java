package java.commonutils.text;

import java.util.regex.Pattern;

public class Regex {
    public static final Pattern HDFS_PATH_REGEX = Pattern.compile("^(hdfs)://(([\\d]{1,3}.[\\d]{1,3}.[\\d]{1,3}.[\\d]{1,3})|([1-9a-zA-Z.]+)):([\\d]{1,5})");

}
