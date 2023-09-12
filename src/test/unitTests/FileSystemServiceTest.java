package unitTests;

import filesystem.ContentFileService;
import filesystem.FileSystemService;
import filesystem.ServiceProvider;
import filesystem.utils.FileSystemUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static integrationTests.FileSystemServiceTest.ADDED_TEST_FILE;
import static integrationTests.FileSystemServiceTest.TEST_FILE;

public class FileSystemServiceTest {

    private static FileSystemService fileSystem;

    private final static List<String> pathsToRemove = new LinkedList<>();

    @BeforeAll
    public static void initialization() {
        ServiceProvider
                .register(ContentFileService.class, new ContentFileService() {
                    @Override
                    public void copyContent(File destinationFile, File fileToCopy) throws IOException {
                        //do nothing
                    }

                    @Override
                    public boolean haveSameContent(File expected, File actual) throws IOException {
                        return false;
                    }
                });

        fileSystem = ServiceProvider.getInstance(FileSystemService.class);
    }

    @AfterEach
    public void tearDown() throws IOException {
        for (var path: pathsToRemove) {
            Files.deleteIfExists(Paths.get(path));
        }
    }

    @Test
    public void addFile_CreatesNotExistingDirectory() {
        //find the TestFile.txt
        var classLoader = FileSystemServiceTest.class.getClassLoader();

        var resourceTestFile = classLoader.getResource(TEST_FILE);
        assertNotNull(resourceTestFile);

        var fileToAddPath = resourceTestFile.getPath();
        var directory = FileSystemUtil.extractDirectory(fileToAddPath) + "/random/";
        var destinationFilePath = directory + "/" + ADDED_TEST_FILE;

        //register the path to be deleted after the test
        pathsToRemove.add(directory);

        assertDoesNotThrow(() ->
                assertFalse(Files.exists(Paths.get(directory)))
        );

        //creates not existing directory '..../random/'
        assertDoesNotThrow(
                () -> fileSystem.addFile(destinationFilePath, fileToAddPath));

        assertDoesNotThrow(() ->
                assertTrue(Files.exists(Paths.get(directory)))
        );
    }

    @Test
    public void addFile_NoSuchFile_ThrowsException() {
        //find the TestFile.txt
        var classLoader = FileSystemServiceTest.class.getClassLoader();

        var resource = classLoader.getResource(TEST_FILE);
        assertNotNull(resource);

        var fileToAdd = resource.getPath();
        var destinationFilePath = FileSystemUtil.extractDirectory(fileToAdd) + "/" + ADDED_TEST_FILE;

        //add file that doesn't exist
        assertThrows(
                NoSuchFileException.class,
                () -> fileSystem.addFile(destinationFilePath, fileToAdd + "blabla")
        );
    }

    @Test
    public void addFile_FileAlreadyExists_ThrowsException() {
        //find the TestFile.txt
        var classLoader = FileSystemServiceTest.class.getClassLoader();

        var resource = classLoader.getResource(TEST_FILE);
        assertNotNull(resource);

        var fileToAdd = resource.getPath();

        //Try to add file that exists
        assertThrows(
                FileAlreadyExistsException.class,
                () -> fileSystem.addFile(fileToAdd, fileToAdd)
        );
    }

    @Test
    public void updateFile_FileAlreadyExists_ThrowsException() {
        //find the TestFile.txt
        var classLoader = FileSystemServiceTest.class.getClassLoader();

        var resource = classLoader.getResource(TEST_FILE);
        assertNotNull(resource);

        var fileToAdd = resource.getPath();

        //update file that doesn't exist
        assertThrows(
                NoSuchFileException.class,
                () -> fileSystem.updateFile(fileToAdd + "bla", fileToAdd)
        );

        //update file that doesn't exist
        assertThrows(
                NoSuchFileException.class,
                () -> fileSystem.updateFile(fileToAdd, fileToAdd + "bla")
        );
    }
}
