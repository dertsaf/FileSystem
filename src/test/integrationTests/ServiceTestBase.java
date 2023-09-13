package integrationTests;

import filesystem.ContentFileService;
import filesystem.FileSystemService;
import filesystem.ServiceProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

abstract class ServiceTestBase {

    public final static String ADDED_TEST_FILE = "AddedFile.txt";
    public final static String TEST_FILE = "TestFile.txt";
    public final static String TEST_FILE2 = "TestFile2.txt";
    protected static FileSystemService fileSystem;
    protected static ContentFileService contentFileService;
    protected final static List<String> filesToRemove = new LinkedList<>();

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
}
