package integrationTests;

import filesystem.ContentFileService;
import filesystem.FileSystemService;
import filesystem.ServiceProvider;
import filesystem.utils.FileSystemUtil;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

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
    public void tearDown() {
        for (var file: filesToRemove) {
            try {
                Files.delete(Paths.get(file));
            } catch (IOException e) {
                //do nothing
            }
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

        //register file for the cleanup:
        filesToRemove.add(destinationFilePath);

        //add File
        fileSystem.addFile(destinationFilePath, fileToAddPath);

        //Verify that both files have the same content:
        compareContentOfTwoFiles(fileToAddPath, destinationFilePath, true);

        //read the added file into TestFile.txt_v2  file:
        var newDestinationFile = destinationFilePath + "_v2";
        //register file for the cleanup
        filesToRemove.add(newDestinationFile);

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

        //register file for the cleanup in case if delete fails
        filesToRemove.add(destinationFilePath);

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

        //register file for the cleanup in case if delete fails
        filesToRemove.add(destinationFilePath);

        //create a file that doesn't exist
        File file = new File(destinationFilePath);
        File file2 = new File(destinationFilePath2);
    }

    private void compareContentOfTwoFiles(String expected, String actual, boolean shouldHaveTheSameContent) throws IOException {
        var areEqual = contentFileService.haveSameContent(Paths.get(expected).toFile(), Paths.get(actual).toFile());
        assertEquals(shouldHaveTheSameContent, areEqual);
    }
}
