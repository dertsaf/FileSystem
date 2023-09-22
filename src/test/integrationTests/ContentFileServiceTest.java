package integrationTests;

import filesystem.utils.FileSystemUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ContentFileServiceTest extends ServiceTestBase {

    @Test
    public void copyContent_CopiedFileHasTheSameContent() throws URISyntaxException, IOException {
        var classLoader = ContentFileServiceTest.class.getClassLoader();

        var resourceTestFile = classLoader.getResource(TEST_FILE);
        assertNotNull(resourceTestFile);
        Path fileToBeCopiedPath = Paths.get(resourceTestFile.toURI());
        File fileToBeCopied = fileToBeCopiedPath.toFile();

        //create new file - copy of the TestFile.txt
        var copyOfTestFile = FileSystemUtil.extractDirectory(fileToBeCopiedPath) + "/"+ UUID.randomUUID();

        File copyFile = Paths.get(copyOfTestFile).toFile();
        contentFileService.copyContent(copyFile, fileToBeCopied);

        assertTrue(haveSameContent(fileToBeCopied, copyFile));
        //Verify that haveSameContent also returns true:
        assertTrue(contentFileService.haveSameContent(fileToBeCopied, copyFile));
    }

    @Test
    public void haveSameContent_ReturnsFalse_WhenFilesHaveDifferentContent() throws URISyntaxException, IOException {
        var classLoader = ContentFileServiceTest.class.getClassLoader();

        var resourceTestFile = classLoader.getResource(TEST_FILE);
        assertNotNull(resourceTestFile);
        Path testFile1Path = Paths.get(resourceTestFile.toURI());
        File testFile1 = testFile1Path.toFile();

        //load TestFile2.txt
        var resourceTestFile2 = classLoader.getResource(TEST_FILE2);
        assertNotNull(resourceTestFile2);
        Path testFile2Path = Paths.get(resourceTestFile2.toURI());
        File testFile2 = testFile2Path.toFile();

        assertFalse(contentFileService.haveSameContent(testFile1, testFile2));
        //Verify that contentFileService.haveSameContent returns the same result
        assertEquals(haveSameContent(testFile1, testFile2), contentFileService.haveSameContent(testFile1, testFile2));
    }

    //This is a helper method to verify if 2 files have the same content.
    // This is a copy-paste from ContentFileServiceImpl. However, since we test this particular class
    // we can't call ContentFileServiceImpl.haveSameContent() in case it gets changed
    private boolean haveSameContent(File expected, File actual) throws IOException {
        if (!expected.exists() || !actual.exists()) {
            return false;
        }

        if (expected.length() != actual.length()) {
            return false;
        }

        try (InputStream is1 = new FileInputStream(expected);
             InputStream is2 = new FileInputStream(actual)) {
            int byte1, byte2;
            do {
                byte1 = is1.read();
                byte2 = is2.read();
                if (byte1 != byte2) {
                    return false;
                }
            } while (byte1 != -1 && byte2 != -1);
        }

        return true;
    }
}
