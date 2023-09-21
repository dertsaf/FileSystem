package integrationTests;

import filesystem.utils.FileSystemUtil;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class FileSystemServiceTest extends ServiceTestBase {

    @Test
    public void addFileReadItAndUpdateIt_Success() throws URISyntaxException, IOException {
        //find the TestFile.txt
        var classLoader = FileSystemServiceTest.class.getClassLoader();

        var resourceTestFile = classLoader.getResource(TEST_FILE);
        assertNotNull(resourceTestFile);

        String fileToAddPath = Paths.get(resourceTestFile.toURI()).toString();

        var resourceTestFile2 = classLoader.getResource(TEST_FILE2);
        assertNotNull(resourceTestFile2);

        var destinationFilePath = FileSystemUtil.extractDirectory(fileToAddPath) + "/"+ ADDED_TEST_FILE;

        //add File
        fileSystem.addFile(destinationFilePath, fileToAddPath);

        //Verify that both files have the same content:
        compareContentOfTwoFiles(fileToAddPath, destinationFilePath, true);

        //read the added file into TestFile.txt_v2  file:
        var newDestinationFile = destinationFilePath + "_v2";

        fileSystem.readFile(destinationFilePath, newDestinationFile);

        //Verify that both files have the same content:
        compareContentOfTwoFiles(destinationFilePath, newDestinationFile, true);

        //update new newDestinationFile with content of TestFile2.txt  and verify that content is different
        String testFile2Path = Paths.get(resourceTestFile2.toURI()).toString();
        fileSystem.updateFile(newDestinationFile, testFile2Path);
        compareContentOfTwoFiles(destinationFilePath, newDestinationFile, false);
    }

    @Test
    public void deleteFile_DeletesExpectedFile() throws URISyntaxException, IOException {
        //find the TestFile.txt
        var classLoader = FileSystemServiceTest.class.getClassLoader();

        var resourceTestFile = classLoader.getResource(TEST_FILE);
        assertNotNull(resourceTestFile);

        String fileToAddPath = Paths.get(resourceTestFile.toURI()).toString();

        var destinationFilePath = FileSystemUtil.extractDirectory(fileToAddPath) + "/"+ ADDED_TEST_FILE;

        //add File
        fileSystem.addFile(destinationFilePath, fileToAddPath);
        assertTrue(Files.exists(Paths.get(destinationFilePath)));

        //deletes destination file
        fileSystem.deleteFile(destinationFilePath);
        assertFalse(Files.exists(Paths.get(destinationFilePath)));
    }

    @Test
    public void copyContent() throws URISyntaxException {
        //find the TestFile.txt
        var classLoader = FileSystemServiceTest.class.getClassLoader();

        var resourceTestFile = classLoader.getResource(TEST_FILE);
        assertNotNull(resourceTestFile);

        String fileToAddPath = Paths.get(resourceTestFile.toURI()).toString();

        var destinationFilePath = FileSystemUtil.extractDirectory(fileToAddPath) + "/"+ UUID.randomUUID();
        var destinationFilePath2 = FileSystemUtil.extractDirectory(fileToAddPath) + "/"+ UUID.randomUUID();

        //create a file that doesn't exist
        File file = new File(destinationFilePath);
        File file2 = new File(destinationFilePath2);
    }

    private void compareContentOfTwoFiles(String expected, String actual, boolean shouldHaveTheSameContent) throws IOException {
        var areEqual = contentFileService.haveSameContent(Paths.get(expected).toFile(), Paths.get(actual).toFile());
        assertEquals(shouldHaveTheSameContent, areEqual);
    }
}
