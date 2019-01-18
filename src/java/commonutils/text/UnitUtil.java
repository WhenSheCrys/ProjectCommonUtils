package java.commonutils.text;

import org.apache.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnitUtil {

    static final Logger logger = Logger.getLogger(UnitUtil.class);

    public static class ByteUtil {

        static final String[] prefix = {"B", "K", "M", "G", "T", "P", "E", "Z", "Y", "B"};

        public static Long fromText(String text) {
            Pattern pattern = Pattern.compile("^(\\d*\\.?\\d+?)([BbKkMmGgTtPpEeZzYy]?)([Bb]?)$");
            Matcher matcher = pattern.matcher(text);
            if (matcher.matches()) {
                String numString = matcher.group(1);
                String unitString1 = matcher.group(2).toUpperCase();
                String unitString2 = matcher.group(3).equals("b") ? "b" : "B";
                double u = Double.valueOf(numString);
                long r = (long) u;

                if (!unitString1.equals("")) {
                    for (String p : prefix) {
                        if (!unitString1.equals(p)) {
                            r *= 1024;
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

        public static String toString(long bytes, boolean human) {
            float r = (float) bytes;
            long unit = 1024L;
            String result = r + "B";
            if (!human) {
                return result;
            }
            int index = 0;
            while (r >= unit && index < prefix.length) {
                r /= unit;
                index++;
            }
            return r + prefix[index];
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
