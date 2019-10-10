package zodiac.java.commonutils.configuration;

import zodiac.java.commonutils.text.StringUtil;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.Properties;

public class PropertiesConfigUtil implements ConfigurationUtil {
    private String fileName;
    private Charset charset = Charset.forName("UTF-8");
    private Properties properties = new Properties();

    public PropertiesConfigUtil(String fileName) {
        this.fileName = fileName;
        this.load();
    }

    public PropertiesConfigUtil(String fileName, Charset charset) {
        this.fileName = fileName;
        this.charset = charset;
        this.load();
    }

    private void load() {
        if (StringUtil.isBlank(fileName)) {
            throw new IllegalArgumentException("fileName should not be empty!");
        }
        File file = new File(fileName);
        if (file.exists()) {
            if (file.isFile()) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), charset));
                    properties.load(reader);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(fileName)), charset));
                properties.load(reader);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean contains(String name) {
        checkKey(name);
        return this.properties.contains(name);
    }

    public int getAsInteger(String name) {
        checkKey(name);
        try {
            return Integer.valueOf(properties.getProperty(name));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int getIntegerOrElse(String name, int defaultValue) {
        checkKey(name);
        try {
            String s = this.properties.getProperty(name);
            return Integer.valueOf(s);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public short getAsShort(String name) {
        checkKey(name);
        try {
            return Short.valueOf(properties.getProperty(name));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public short getShortOrElse(String name, short defaultValue) {
        checkKey(name);
        try {
            String s = this.properties.getProperty(name);
            return Short.valueOf(s);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public float getAsFloat(String name) {
        checkKey(name);
        try {
            return Float.valueOf(properties.getProperty(name));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public float getFloatOrElse(String name, float defaultValue) {
        checkKey(name);
        try {
            String s = this.properties.getProperty(name);
            return Float.valueOf(s);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public double getAsDouble(String name) {
        checkKey(name);
        try {
            return Double.valueOf(properties.getProperty(name));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public double getDoubleOrElse(String name, double defaultValue) {
        checkKey(name);
        try {
            String s = this.properties.getProperty(name);
            return Double.valueOf(s);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public String[] getAsStringArray(String name, String separator) {
        checkKey(name);
        return this.get(name).split(separator, -1);
    }

    public int[] getAsIntegerArray(String name, String separator) {
        String[] strings = this.get(name).split(separator, -1);
        int[] ints = new int[strings.length];
        for (int i = 0; i < strings.length; i++) {
            try {
                ints[i] = Integer.valueOf(strings[i]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return ints;
    }

    public float[] getAsFloatArray(String name, String separator) {
        String[] strings = this.get(name).split(separator, -1);
        float[] floats = new float[strings.length];
        for (int i = 0; i < strings.length; i++) {
            try {
                floats[i] = Float.valueOf(strings[i]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return floats;
    }

    public short[] getAsShortArray(String name, String separator) {
        String[] strings = this.get(name).split(separator, -1);
        short[] shorts = new short[strings.length];
        for (int i = 0; i < strings.length; i++) {
            try {
                shorts[i] = Short.valueOf(strings[i]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return shorts;
    }

    public double[] getAsDoubleArray(String name, String separator) {
        String[] strings = this.get(name).split(separator, -1);
        double[] doubles = new double[strings.length];
        for (int i = 0; i < strings.length; i++) {
            try {
                doubles[i] = Double.valueOf(strings[i]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return doubles;
    }

    public String get(String name) {
        checkKey(name);
        return this.properties.getProperty(name);
    }

    public String getOrElse(String name, String defaultValue) {
        checkKey(name);
        return this.properties.getProperty(name, defaultValue);
    }

    private void checkKey(String key) {
        assert contains(key) : "key " + key + " not exists";
    }
}
