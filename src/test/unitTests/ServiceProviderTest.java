package unitTests;

import filesystem.ContentFileService;
import filesystem.FileSystemService;
import filesystem.FileSystemServiceImpl;
import filesystem.ServiceProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceProviderTest {

    @Test
    public void getInstance_ReturnsDefaultImplementation() {
        assertNotNull(ServiceProvider.getInstance(ContentFileService.class));
        assertNotNull(ServiceProvider.getInstance(FileSystemService.class));
    }

    @Test
    public void getInstance_ThrowsException_whenImplementationNotProvided() {
        assertThrows(RuntimeException.class,
                () -> ServiceProvider.getInstance(ServiceProviderTest.class));
    }

    @Test
    public void getInstance_ThrowsException_WhenTriesToOverrideImplementation() {
        ServiceProvider.getInstance(FileSystemService.class);
        var newFileSystem = new FileSystemServiceImpl();
        assertThrows(RuntimeException.class,
                () -> ServiceProvider.register(FileSystemService.class, newFileSystem));
    }
}
