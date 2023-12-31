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

    private final static List<Path> pathsToRemove = new LinkedList<>();

    @BeforeAll
    public static void initialization() {
        ServiceProvider
                .register(ContentFileService.class, new ContentFileService() {
                    @Override
                    public void saveFileToFileSystem(String destinationFile, File fileToCopy) throws IOException {}
                    @Override
                    public void readFileFromFileSystem(String fileToReadFrom, File fileToCopyTo) throws IOException {}
                    @Override
                    public void deleteFileFromFileSystem(String fileToDelete) throws IOException {}
                    @Override
                    public void updateFileInFileSystem(String destinationFile, File fileToUpdateWith) throws IOException {}
                    @Override
                    public void copyContent(File destinationFile, File fileToCopy) {}
                    @Override
                    public boolean haveSameContent(File expected, File actual) {
                        return false;
                    }

                    @Override
                    public void cleanVirtualFileSystem() throws IOException, URISyntaxException {

                    }
                });

        fileSystem = ServiceProvider.getInstance(FileSystemService.class);
    }

    @AfterEach
    public void tearDown() throws IOException {
        for (var path: pathsToRemove) {
            Files.deleteIfExists(path);
        }
    }

    @Test
    public void addAndReadFile_ThrowsException_WhenFileDoesNotExist() throws URISyntaxException {
        //find the TestFile.txt
        var classLoader = FileSystemServiceTest.class.getClassLoader();

        URL resource = classLoader.getResource(TEST_FILE);
        assertNotNull(resource);

        Path fileToAddPath = Paths.get(resource.toURI());

        var destinationFilePath = FileSystemUtil.extractDirectory(fileToAddPath) + "/" + ADDED_TEST_FILE;

        //add file that doesn't exist
        assertThrows(
                NoSuchFileException.class,
                () -> fileSystem.addFile(destinationFilePath, fileToAddPath + "blabla")
        );

        //read file that doesn't exist
        assertDoesNotThrow(
                () -> fileSystem.readFile(fileToAddPath + "blabla", destinationFilePath)
        );
    }

    @Test
    public void addAndReadFile_DoesNotThrowsException_WhenDestinationFileExist() throws URISyntaxException {
        //find the TestFile.txt
        var classLoader = FileSystemServiceTest.class.getClassLoader();

        var resource = classLoader.getResource(TEST_FILE);
        assertNotNull(resource);

        Path fileToAddPath = Paths.get(resource.toURI());

        var fileToAdd = fileToAddPath.toString();

        //Try to add file that exists
        assertDoesNotThrow(
                () -> fileSystem.addFile(fileToAdd, fileToAdd)
        );

        //Try to add file that exists
        assertDoesNotThrow(
                () -> fileSystem.readFile(fileToAdd, fileToAdd)
        );
    }

    @Test
    public void updateFile_OnlyThrowsException_WhenFileToUpdateWithDoesNotExist() throws URISyntaxException {
        //find the TestFile.txt
        var classLoader = FileSystemServiceTest.class.getClassLoader();

        URL resource = classLoader.getResource(TEST_FILE);
        assertNotNull(resource);

        //URISyntaxException shouldn't be thrown:
        assertDoesNotThrow(resource::toURI);
        Path fileToAddPath = Paths.get(resource.toURI());
        String fileToAdd = fileToAddPath.toString();

        //update file that doesn't exist
        assertDoesNotThrow(
                () -> fileSystem.updateFile(fileToAdd + "bla", fileToAdd)
        );

        //update file that doesn't exist
        assertThrows(
                NoSuchFileException.class,
                () -> fileSystem.updateFile(fileToAdd, fileToAdd + "bla")
        );
    }
}
