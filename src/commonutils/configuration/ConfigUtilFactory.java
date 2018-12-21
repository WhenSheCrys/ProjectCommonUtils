package commonutils.configuration;

import java.nio.charset.Charset;

public class ConfigUtilFactory {

    public static ConfigUtilBuilder builder() {
        return new ConfigUtilBuilder();
    }


    public static class ConfigUtilBuilder {

        private ConfigType configType = ConfigType.PROPERTIES;
        private Charset charset = Charset.forName("UTF-8");
        private String fileName;

        public ConfigUtilBuilder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public ConfigUtilBuilder setCharset(Charset charset) {
            this.charset = charset;
            return this;
        }

        public ConfigUtilBuilder setType(ConfigType configType) {
            this.configType = configType;
            return this;
        }

        public ConfigurationUtilInterface load() {
            ConfigurationUtilInterface configurationUtilInterface = null;
            switch (this.configType) {
                case INI:
                    break;
                case PROPERTIES:
                    configurationUtilInterface = new PropertiesUtil(fileName, charset);
                    break;
                case JSON:
                    break;
                case YAML:
                    break;
                default:
                    throw new IllegalArgumentException("No such type:" + configType.name());
            }
            return configurationUtilInterface;
        }

    }
}
