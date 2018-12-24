package commonutils.configuration;

import java.nio.charset.Charset;

public class JsonUtil implements ConfigurationUtil {

    private String fileName;
    private Charset charset = Charset.forName("UTF-8");

    public JsonUtil(String fileName, Charset charset) {
        this.fileName = fileName;
        this.charset = charset;
    }

    @Override
    public ConfigurationUtil load() {
        return null;
    }

    @Override
    public boolean contains(String name) {
        return false;
    }

    @Override
    public int getAsInteger(String name) {
        return 0;
    }

    @Override
    public int getIntegerOrElse(String name, int defaultValue) {
        return 0;
    }

    @Override
    public short getAsShort(String name) {
        return 0;
    }

    @Override
    public short getShortOrElse(String name, short defaultValue) {
        return 0;
    }

    @Override
    public float getAsFloat(String name) {
        return 0;
    }

    @Override
    public float getFloatOrElse(String name, float defaultValue) {
        return 0;
    }

    @Override
    public double getAsDouble(String name) {
        return 0;
    }

    @Override
    public double getDoubleOrElse(String name, double defaultValue) {
        return 0;
    }

    @Override
    public String[] getAsStringArray(String name, String separator) {
        return new String[0];
    }

    @Override
    public int[] getAsIntegerArray(String name, String separator) {
        return new int[0];
    }

    @Override
    public float[] getAsFloatArray(String name, String separator) {
        return new float[0];
    }

    @Override
    public short[] getAsShortArray(String name, String separator) {
        return new short[0];
    }

    @Override
    public double[] getAsDoubleArray(String name, String separator) {
        return new double[0];
    }

    @Override
    public String get(String name) {
        return null;
    }

    @Override
    public String getOrElse(String name, String defaultValue) {
        return null;
    }
}
