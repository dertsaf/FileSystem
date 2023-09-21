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

    @BeforeAll
    public static void initialization() {
        fileSystem = ServiceProvider.getInstance(FileSystemService.class);
        contentFileService = ServiceProvider.getInstance(ContentFileService.class);
    }
}
