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
import java.net.URISyntaxException;
import java.net.URL;
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
    public void addFile_CreatesNotExistingDirectory() throws URISyntaxException {
        //find the TestFile.txt
        var classLoader = FileSystemServiceTest.class.getClassLoader();

        URL resourceTestFile = classLoader.getResource(TEST_FILE);
        assertNotNull(resourceTestFile);

        //URISyntaxException shouldn't be thrown:
        assertDoesNotThrow(resourceTestFile::toURI);

        Path fileToAddPath = Paths.get(resourceTestFile.toURI());
        String directory = FileSystemUtil.extractDirectory(fileToAddPath) +  "/random/";
        String destinationFilePath = directory + "/" + ADDED_TEST_FILE;

        //register the path to be deleted after the test
        pathsToRemove.add(directory);

        assertDoesNotThrow(() ->
                assertFalse(Files.exists(Paths.get(directory)))
        );

        //creates not existing directory '..../random/'
        assertDoesNotThrow(
                () -> fileSystem.addFile(destinationFilePath, fileToAddPath.toString()));

        assertDoesNotThrow(() ->
                assertTrue(Files.exists(Paths.get(directory)))
        );
    }

    @Test
    public void addFile_NoSuchFile_ThrowsException() throws URISyntaxException {
        //find the TestFile.txt
        var classLoader = FileSystemServiceTest.class.getClassLoader();

        URL resource = classLoader.getResource(TEST_FILE);
        assertNotNull(resource);
        //URISyntaxException shouldn't be thrown:
        assertDoesNotThrow(resource::toURI);

        Path fileToAddPath = Paths.get(resource.toURI());

        var destinationFilePath = FileSystemUtil.extractDirectory(fileToAddPath) + "/" + ADDED_TEST_FILE;

        //add file that doesn't exist
        assertThrows(
                NoSuchFileException.class,
                () -> fileSystem.addFile(destinationFilePath, fileToAddPath.toString() + "blabla")
        );
    }

    @Test
    public void addFile_FileAlreadyExists_ThrowsException() throws URISyntaxException {
        //find the TestFile.txt
        var classLoader = FileSystemServiceTest.class.getClassLoader();

        var resource = classLoader.getResource(TEST_FILE);
        assertNotNull(resource);
        //URISyntaxException shouldn't be thrown:
        assertDoesNotThrow(resource::toURI);

        Path fileToAddPath = Paths.get(resource.toURI());

        var fileToAdd = fileToAddPath.toString();

        //Try to add file that exists
        assertThrows(
                FileAlreadyExistsException.class,
                () -> fileSystem.addFile(fileToAdd, fileToAdd)
        );
    }

    @Test
    public void updateFile_FileAlreadyExists_ThrowsException() throws URISyntaxException {
        //find the TestFile.txt
        var classLoader = FileSystemServiceTest.class.getClassLoader();

        URL resource = classLoader.getResource(TEST_FILE);
        assertNotNull(resource);

        //URISyntaxException shouldn't be thrown:
        assertDoesNotThrow(resource::toURI);
        Path fileToAddPath = Paths.get(resource.toURI());
        String fileToAdd = fileToAddPath.toString();

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
