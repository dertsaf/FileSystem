package integrationTests;

import filesystem.ContentFileService;
import filesystem.FileSystemService;
import filesystem.ServiceProvider;
import filesystem.utils.FileSystemUtil;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class FileSystemServiceTest extends ServiceTestBase {

    @BeforeAll
    public static void beforeEach() {
        fileSystem = ServiceProvider.getInstance(FileSystemService.class);
        contentFileService = ServiceProvider.getInstance(ContentFileService.class);

        try {
            contentFileService.cleanVirtualFileSystem();
        } catch (IOException | URISyntaxException e) {
            //do nothing
        }
    }

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

        //read the added file into TestFile_v2.txt  file:
        var newDestinationFile = FileSystemUtil.extractDirectory(fileToAddPath) + "/" + "TestFile_v2.txt";
        var newDestinationFile2 = FileSystemUtil.extractDirectory(fileToAddPath) + "/" + "TestFile_v3.txt";

        fileSystem.readFile(destinationFilePath, newDestinationFile);
        //register file for the cleanup:
        //filesToRemove.add(newDestinationFile);

        //Verify that both files have the same content:
        compareContentOfTwoFiles(fileToAddPath, newDestinationFile, true);

        //update new destinationFilePath with content of TestFile2.txt  and verify that content is different
        String testFile2Path = Paths.get(resourceTestFile2.toURI()).toString();
        fileSystem.updateFile(destinationFilePath, testFile2Path);

        fileSystem.readFile(destinationFilePath, newDestinationFile2);
        //register file for the cleanup:
        //filesToRemove.add(destinationFilePath + "v2");

        compareContentOfTwoFiles(fileToAddPath, newDestinationFile2, false);
    }

    @Test
    public void deleteFile_DeletesExpectedFile() throws URISyntaxException, IOException {
        //find the TestFile.txt
        var classLoader = FileSystemServiceTest.class.getClassLoader();

        var resourceTestFile = classLoader.getResource(TEST_FILE);
        assertNotNull(resourceTestFile);

        String fileToAddPath = Paths.get(resourceTestFile.toURI()).toString();

        var destinationFilePath = FileSystemUtil.extractDirectory(fileToAddPath) + "/"+ ADDED_TEST_FILE;
        var fileToReadTo = FileSystemUtil.extractDirectory(fileToAddPath) + "/" + UUID.randomUUID();

        //register file for the cleanup in case test fails:
        filesToRemove.add(fileToReadTo);

        //add File
        fileSystem.addFile(destinationFilePath, fileToAddPath);
        fileSystem.readFile(destinationFilePath, fileToReadTo);
        assertTrue(Files.exists(Paths.get(fileToReadTo)));

        //deletes destination file
        fileSystem.deleteFile(destinationFilePath);
        assertThrows(
                NoSuchFileException.class,
                () -> fileSystem.readFile(destinationFilePath, fileToReadTo + "_v2")
        );
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
