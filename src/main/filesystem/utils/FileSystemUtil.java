package filesystem.utils;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static filesystem.utils.FileMetaData.METADATA_SEPARATOR;

public class FileSystemUtil {

    private static final String PAIRS_SEPARATOR = ";";

    private static final String KEY_VALUE_SEPARATOR = ":";

    private FileSystemUtil(){}

    public static Path extractDirectory(String filePath) {
        Path path = Paths.get(filePath);
        return extractDirectory(path);
    }

    public static Path extractDirectory(Path path) {
        return path.getParent();
    }

    public static String serializeMap(Map<String, FileMetaData> map) {
        if (map == null || map.isEmpty())
            return "";

        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, FileMetaData> entry : map.entrySet()) {
            sb.append(entry.getKey());
            sb.append(KEY_VALUE_SEPARATOR);
            sb.append(entry.getValue().toString());
            sb.append(PAIRS_SEPARATOR);
        }

        return sb.toString();
    }

    public static Map<String, FileMetaData> deserializeMap(String serializedMap) {
        Map<String, FileMetaData> map = new HashMap<>();

        if (serializedMap == null || serializedMap.isEmpty()) {
            return map;
        }

        String[] mapEntries = serializedMap.split(PAIRS_SEPARATOR);
        for (String entry : mapEntries) {
            String[] keyValue = entry.split(KEY_VALUE_SEPARATOR);
            if (keyValue.length == 2) {
                String key = keyValue[0];

                String[] metaDataArray = keyValue[1].split(METADATA_SEPARATOR);
                if (metaDataArray.length == 2) {
                    var metaData = new FileMetaData(Long.parseLong(metaDataArray[0]), Long.parseLong(metaDataArray[1]));
                    map.put(key, metaData);
                }
            }
        }

        return map;
    }
}
