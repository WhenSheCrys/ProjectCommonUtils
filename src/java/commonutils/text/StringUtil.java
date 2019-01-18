package java.commonutils.text;

public class StringUtil {

    public static boolean isEmpty(String input) {
        return null == input || input.length() == 0;
    }

    public static boolean isBlank(String input) {
        return isEmpty(input) || isEmpty(input.trim());
    }

    public static boolean isNotEmpty(String input) {
        return !isEmpty(input);
    }

    public static boolean isNotBlank(String input) {
        return !isBlank(input);
    }

}
