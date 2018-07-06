package commonutils.text;

import org.apache.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pattern类在Scala中无法正确解析，原因未知，所以采用Java编写。
 */
public class UnitUtil {

    static final Logger logger = Logger.getLogger(UnitUtil.class);

    public static class ByteUtil {

        static final String[] prefix = {"K", "M", "G", "T", "P", "E", "Z", "Y"};

        public static Long fromText(String text) {
            Pattern pattern = Pattern.compile("^(\\d+\\.?\\d+?)([KkMmGgTtPpEeZzYy]?)([Bb]?)$");
            Matcher matcher = pattern.matcher(text);
            if (matcher.matches()) {
                String numString = matcher.group(1);
                String unitString1 = matcher.group(2);
                String unitString2 = matcher.group(3).equals("b") ? "b" : "B";
                double u = Double.valueOf(numString);
                long r = (long) u;

                if (!unitString1.equals("")) {
                    for (int i = 1; i <= prefix.length; i++) {
                        if (!unitString1.toUpperCase().equals(prefix[i - 1])) {
                            r = (long) (u * (1L << ((long) (i + 1) * 10L)));
                        } else {
                            break;
                        }
                    }
                }
                if (unitString2.equals("b")) {
                    r = (long) Math.ceil((double) r / 8D);
                }
                return r;
            } else {
                throw new IllegalArgumentException(String.format("Input %s not match!", text));
            }
        }

        static String toString(long bytes, boolean human) {

            float r = (float) bytes;
            long unit = 1L << 10L;
            String result = r + "B";

            for (int i = 1; i <= prefix.length; i++) {
                if (r >= unit) {
                    r = r / unit;
                } else {
                    result = r + prefix[i - 1] + "B";
                    break;
                }
            }
            return result;
        }

        public static String toString(long bytes) {
            return toString(bytes, false);
        }

    }

    public class Binary {
        public Binary() {

        }

        public Binary(Byte[] bytes) {

        }
    }

    public class Octal {

    }

    public class Decimal {

    }

    public class Hex {

    }

}
