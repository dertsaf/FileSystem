package unitTests;

import filesystem.ContentFileService;
import filesystem.ServiceProvider;
import filesystem.utils.FileSystemUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static integrationTests.ServiceTestBase.ADDED_TEST_FILE;
import static integrationTests.ServiceTestBase.TEST_FILE;
import static org.junit.jupiter.api.Assertions.*;

public class ContentFileServiceTest {
    private static ContentFileService contentFileService;

    @BeforeAll
    public static void initialization() {
        contentFileService = ServiceProvider.getInstance(ContentFileService.class);
    }

    @Test
    public void saveAndReadFile_ThrowsException_WhenFileDoesNotExist() throws URISyntaxException {
        //find the TestFile.txt
        var classLoader = ContentFileServiceTest.class.getClassLoader();

        URL resource = classLoader.getResource(TEST_FILE);
        assertNotNull(resource);

        Path fileToAddPath = Paths.get(resource.toURI());

        var destinationFilePath = FileSystemUtil.extractDirectory(fileToAddPath) + "/" + ADDED_TEST_FILE;

        //add file that doesn't exist
        assertThrows(
                NoSuchFileException.class,
                () -> contentFileService.saveFileToFileSystem(destinationFilePath, new File( "blabla"))
        );


        //read file that doesn't exist
        assertThrows(
                NoSuchFileException.class,
                () -> contentFileService.readFileFromFileSystem("blabla", fileToAddPath.toFile())
        );
    }

    @Test
    public void updateFile_ThrowsException_WhenFileToUpdatedFileDoesNotExist() throws URISyntaxException {
        //find the TestFile.txt
        var classLoader = ContentFileServiceTest.class.getClassLoader();

        URL resource = classLoader.getResource(TEST_FILE);
        assertNotNull(resource);

        //URISyntaxException shouldn't be thrown:
        assertDoesNotThrow(resource::toURI);
        Path fileToAddPath = Paths.get(resource.toURI());
        String fileToAdd = fileToAddPath.toString();

        //update file that doesn't exist
        assertThrows(
                NoSuchFileException.class,
                () -> contentFileService.updateFileInFileSystem("bla", fileToAddPath.toFile())
        );

    }
}
