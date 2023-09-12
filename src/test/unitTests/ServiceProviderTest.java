package unitTests;

import filesystem.ContentFileService;
import filesystem.FileSystemService;
import filesystem.FileSystemServiceImpl;
import filesystem.ServiceProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceProviderTest {

    @Test
    public void getInstance_returnsDefaultImplementation() {
        assertNotNull(ServiceProvider.getInstance(ContentFileService.class));
        assertNotNull(ServiceProvider.getInstance(FileSystemService.class));
    }

    @Test
    public void getInstance_throwsException_whenImplementationNotProvided() {
        assertThrows(RuntimeException.class,
                () -> ServiceProvider.getInstance(ServiceProviderTest.class));
    }

    @Test
    public void getInstance_returnsOverriddenImplementation() {
        var fileSystemImpl = ServiceProvider.getInstance(FileSystemService.class);
        var newFileSystem = new FileSystemServiceImpl();

        ServiceProvider.register(FileSystemService.class, newFileSystem);

        assertNotEquals(fileSystemImpl, ServiceProvider.getInstance(FileSystemService.class));
        assertEquals(newFileSystem, ServiceProvider.getInstance(FileSystemService.class));
    }
}
