package zodiac.java.commonutils.array;

import scala.actors.threadpool.Arrays;

public class ArrayUtil {

    public static <T> boolean isNotEmpty(T... ts) {
        return null != ts && ts.length > 0;
    }

    public static <T> boolean isEmpty(T... ts) {
        return !isNotEmpty(ts);
    }

    public static <T extends Comparable> T max(T... ts) {
        return minMax(false, ts);
    }

    public static <T extends Comparable> T min(T... ts) {
        return minMax(true, ts);
    }

    private static <T extends Comparable> T minMax(boolean min, T... ts) {
        assert ArrayUtil.isNotEmpty(ts);
        T maxMin = ts[0];
        if (min) {
            for (T number : ts) {
                if (number.compareTo(maxMin) < 0) {
                    maxMin = number;
                }
            }
        } else {
            for (T number : ts) {
                if (number.compareTo(maxMin) > 0) {
                    maxMin = number;
                }
            }
        }
        return maxMin;
    }

    public static <T> String mkString(String separator, T... ts) {
        return String.join(separator, Arrays.asList(ts));
    }
}
