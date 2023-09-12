package integrationTests;

import filesystem.ContentFileService;
import filesystem.FileSystemService;
import filesystem.ServiceProvider;
import filesystem.utils.FileSystemUtil;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileSystemServiceTest {

    public final static String ADDED_TEST_FILE = "AddedFile.txt";
    public final static String TEST_FILE = "TestFile.txt";
    private final static String TEST_FILE2 = "TestFile2.txt";
    private static FileSystemService fileSystem;
    private static ContentFileService contentFileService;
    private final static List<String> filesToRemove = new LinkedList<>();

    @BeforeAll
    public static void initialization() {
        fileSystem = ServiceProvider.getInstance(FileSystemService.class);
        contentFileService = ServiceProvider.getInstance(ContentFileService.class);
    }

    @AfterEach
    public void tearDown() throws IOException {
        for (var file: filesToRemove) {
            try {
                fileSystem.deleteFile(file);
            } catch (NoSuchFileException e) {
                //do nothing
            }
        }
    }

    @Test
    public void addFile_ReadIt_Update_Then_Delete_Success() throws URISyntaxException {
        //find the TestFile.txt
        var classLoader = FileSystemServiceTest.class.getClassLoader();

        var resourceTestFile = classLoader.getResource(TEST_FILE);
        assertNotNull(resourceTestFile);
        //URISyntaxException shouldn't be thrown:
        assertDoesNotThrow(resourceTestFile::toURI);

        String fileToAddPath = Paths.get(resourceTestFile.toURI()).toString();

        var resourceTestFile2 = classLoader.getResource(TEST_FILE2);
        assertNotNull(resourceTestFile2);
        //URISyntaxException shouldn't be thrown:
        assertDoesNotThrow(resourceTestFile2::toURI);

        var destinationFilePath = FileSystemUtil.extractDirectory(fileToAddPath) + "/"+ ADDED_TEST_FILE;

        //register file for the cleanup:
        filesToRemove.add(destinationFilePath);

        //add File
        assertDoesNotThrow(() -> fileSystem.addFile(destinationFilePath, fileToAddPath));

        //Verify that both files have the same content:
        compareContentOfTwoFiles(fileToAddPath, destinationFilePath, true);

        //read the added file into TestFile.txt_v2  file:
        var newDestinationFile = destinationFilePath + "_v2";
        //fallback: register file for the cleanup in case it's not properly deleted in the end of the method
        filesToRemove.add(newDestinationFile);

        assertDoesNotThrow(() -> fileSystem.readFile(destinationFilePath, newDestinationFile));

        //Verify that both files have the same content:
        compareContentOfTwoFiles(destinationFilePath, newDestinationFile, true);

        //update new newDestinationFile with content of TestFile2.txt  and verify that content is different
        String testFile2Path = Paths.get(resourceTestFile2.toURI()).toString();
        assertDoesNotThrow(() -> fileSystem.updateFile(newDestinationFile, testFile2Path));
        compareContentOfTwoFiles(destinationFilePath, newDestinationFile, false);

        //delete AddedFile.txt_v2:
        assertDoesNotThrow(() -> fileSystem.deleteFile(newDestinationFile));
    }

    private void compareContentOfTwoFiles(String expected, String actual, boolean shouldHaveTheSameContent) {
        assertDoesNotThrow(() -> {
            var areEqual = contentFileService.haveSameContent(Paths.get(expected).toFile(), Paths.get(actual).toFile());
            assertEquals(shouldHaveTheSameContent, areEqual);
        });
    }
}
