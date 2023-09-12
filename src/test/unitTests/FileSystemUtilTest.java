package unitTests;

import filesystem.utils.FileSystemUtil;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileSystemUtilTest {


    @ParameterizedTest
    @CsvSource({
            "/folder1/folder2, /file1",
            "/ , file1",
            "/ , "
    })
    public void extractDirectory_AllPathsCorrect_DoesNotThrowException(String directory, String file) {
        var actualResult = FileSystemUtil.extractDirectory(directory + file);
        assertEquals(directory, actualResult.toString());
    }

}
