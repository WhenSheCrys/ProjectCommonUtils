package commonutils.configuration;

public interface ConfigurationUtil {

    ConfigurationUtil load();

    boolean contains(String name);

    int getAsInteger(String name);

    int getIntegerOrElse(String name, int defaultValue);

    short getAsShort(String name);

    short getShortOrElse(String name, short defaultValue);

    float getAsFloat(String name);

    float getFloatOrElse(String name, float defaultValue);

    double getAsDouble(String name);

    double getDoubleOrElse(String name, double defaultValue);

    String[] getAsStringArray(String name, String separator);

    int[] getAsIntegerArray(String name, String separator);

    float[] getAsFloatArray(String name, String separator);

    short[] getAsShortArray(String name, String separator);

    double[] getAsDoubleArray(String name, String separator);

    String get(String name);

    String getOrElse(String name, String defaultValue);

}
