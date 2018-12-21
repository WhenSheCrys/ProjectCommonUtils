package commonutils.math;

import commonutils.array.ArrayUtil;

/**
 * 由于Scala的数值类型引用自Java的类，不是原生类型，存在计算性能问题，故采用Java编写
 */
public class MathUtil {

    public double max(Double... doubles) {
        return ArrayUtil.max(doubles);
    }

    public double min(Double... doubles) {
        return ArrayUtil.max(doubles);
    }

    public double max(Integer... doubles) {
        return ArrayUtil.max(doubles);
    }

    public double min(Integer... doubles) {
        return ArrayUtil.max(doubles);
    }

    public double max(Short... doubles) {
        return ArrayUtil.max(doubles);
    }

    public double min(Short... doubles) {
        return ArrayUtil.max(doubles);
    }

    public double max(Float... doubles) {
        return ArrayUtil.max(doubles);
    }

    public double min(Float... doubles) {
        return ArrayUtil.max(doubles);
    }

    public double max(Byte... doubles) {
        return ArrayUtil.max(doubles);
    }

    public double min(Byte... doubles) {
        return ArrayUtil.max(doubles);
    }

}
