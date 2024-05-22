package SpringPoc.utilities;

import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

public class YamlUtil {

    static Map<?, ?> yamlMap;
    static String path = "config/testdata/configdata.yaml";

    public static void initializeObject() {
        try {
            Reader reader = new FileReader(path);
            Yaml yml = new Yaml();
            yamlMap = (Map<?, ?>) yml.load(reader);
            try {
                reader.close();
            } catch (IOException e) {
            }
        } catch (FileNotFoundException e) {
        }
    }

    public static String getYamlData(String strKey, String objString) {
        try {
            if (objString.isEmpty()) {
                String[] keys = strKey.split("\\.");
                Map<?, ?> currentMap = yamlMap;

                for (String key : keys) {
                    if (currentMap.containsKey(key) && currentMap.get(key) instanceof Map) {
                        currentMap = (Map<String, Object>) currentMap.get(key);
                    } else if (currentMap.containsKey(key)) {
                        return currentMap.get(key).toString();
                    } else {
                        return "";  // Key not found
                    }
                }
            }
        } catch (Exception e) {
            objString = "";
        }
        return objString;
    }

    public static String getYamlData(String keyPath) {
        try {
            String[] keys = keyPath.split("\\.");
            Map<?, ?> currentMap = yamlMap;

            for (String key : keys) {
                if (currentMap.containsKey(key) && currentMap.get(key) instanceof Map) {
                    currentMap = (Map<String, Object>) currentMap.get(key);
                } else if (currentMap.containsKey(key)) {
                    return currentMap.get(key).toString();
                } else {
                    return "";  // Key not found
                }
            }
            return "";  // The loop finished without returning a value
        } catch (Exception e) {
            return "";
        }
    }
}
