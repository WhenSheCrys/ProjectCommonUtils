package zodiac.java.commonutils.configuration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.Charset;

public class JsonConfigUtil implements ConfigurationUtil {

    private JSONObject jsonObject;

    private String fileName;
    private Charset charset = Charset.forName("UTF-8");

    public JsonConfigUtil(String fileName, Charset charset) {
        this.fileName = fileName;
        this.charset = charset;
        load();
    }

    public void load() {
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while (reader.ready() && (line = reader.readLine()) != null) {
                sb.append(line);
            }
            jsonObject = new JSONObject(sb.toString());
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject = new JSONObject();
        }
    }

    @Override
    public boolean contains(String name) {
        return jsonObject.has(name);
    }

    @Override
    public int getAsInteger(String name) {
        return jsonObject.getInt(name);
    }

    @Override
    public int getIntegerOrElse(String name, int defaultValue) {
        if (contains(name)) {
            return jsonObject.getInt(name);
        } else {
            return defaultValue;
        }
    }

    @Override
    public short getAsShort(String name) {
        if (jsonObject.getInt(name) > Short.MAX_VALUE) {
            return Short.MAX_VALUE;
        } else {
            return (short) jsonObject.getInt(name);
        }
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

    public JSONObject getJsonObject(String name) {
        return jsonObject.getJSONObject(name);
    }

    public JSONArray getJsonArray(String name) {
        return jsonObject.getJSONArray(name);
    }
}
