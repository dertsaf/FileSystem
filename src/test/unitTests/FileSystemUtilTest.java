package unitTests;

import filesystem.utils.FileMetaData;
import filesystem.utils.FileSystemUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileSystemUtilTest {


    @ParameterizedTest
    @CsvSource({
            "/folder1/folder2, /file1",
            "/ , file1",
            "/ , "
    })
    public void extractDirectory_DoesNotThrowException_WhenAllPathsAreCorrect(String directory, String file) {
        var actualResult = FileSystemUtil.extractDirectory(directory + file);
        assertEquals(directory, actualResult.toString());
    }

    @Test
    public void serializeMap_ReturnsEmptyString_WhenMapIsEmpty() {
        assertEquals("", FileSystemUtil.serializeMap(null));
        assertEquals("", FileSystemUtil.serializeMap(new HashMap<>()));
    }

    @Test
    public void serializeDeserializedMap_() {
        Map<String, FileMetaData> testMap = Map.of(
                "testFile", new FileMetaData(1L, 5L),
                "testFile2", new FileMetaData(2L, 10L)
        );
        String serializedMap = FileSystemUtil.serializeMap(testMap);
        var deserializedMap = FileSystemUtil.deserializeMap(serializedMap);

        assertEquals(testMap.size(), deserializedMap.size());
        for (Map.Entry<String, FileMetaData> entry : testMap.entrySet()) {
            assertTrue(deserializedMap.containsKey(entry.getKey()));
            assertEquals(entry.getValue().fileLength(), deserializedMap.get(entry.getKey()).fileLength());
            assertEquals(entry.getValue().startFilePosition(), deserializedMap.get(entry.getKey()).startFilePosition());
        }

    }

}
