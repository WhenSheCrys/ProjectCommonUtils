package j.commonutils.text;

import java.nio.charset.Charset;
import java.util.Base64;

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

    public static String reverse(String input) {
        StringBuilder builder = new StringBuilder();
        for (int i = input.length() - 1; i > 0; i--) {
            builder.append(input.charAt(i));
        }
        return builder.toString();
    }

    public static String toBase64(String input, Charset charset) {
        return new String(Base64.getEncoder().encode(input.getBytes()), charset);
    }

    public static String fromBase64(String input, Charset charset) {
        return new String(Base64.getDecoder().decode(input), charset);
    }
}
