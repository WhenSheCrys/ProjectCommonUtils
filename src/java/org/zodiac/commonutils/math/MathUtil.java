package org.zodiac.commonutils.math;

import org.zodiac.commonutils.array.ArrayUtil;

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

    public int max(Integer... integers) {
        return ArrayUtil.max(integers);
    }

    public double min(Integer... integers) {
        return ArrayUtil.max(integers);
    }

    public short max(Short... shorts) {
        return ArrayUtil.max(shorts);
    }

    public short min(Short... shorts) {
        return ArrayUtil.max(shorts);
    }

    public float max(Float... floats) {
        return ArrayUtil.max(floats);
    }

    public float min(Float... floats) {
        return ArrayUtil.max(floats);
    }

    public byte max(Byte... bytes) {
        return ArrayUtil.max(bytes);
    }

    public byte min(Byte... bytes) {
        return ArrayUtil.max(bytes);
    }

}
