package filesystem;

import java.util.Map;
import java.util.HashMap;

public class ServiceProvider {

    private ServiceProvider(){}

    private static Map<Class<?>, Object> serviceRegistryMap;

    public static  <T> void register(Class<T> serviceType, T serviceInstance) {
        getServiceRegistry().put(serviceType, serviceInstance);
    }

    public static <T> T getInstance(Class<T> serviceType) {
        Object serviceInstance = getServiceRegistry().get(serviceType);
        if (serviceInstance == null) {
            throw new RuntimeException("Service not registered: " + serviceType.getName());
        }
        return serviceType.cast(serviceInstance);
    }

    private static Map<Class<?>, Object> getServiceRegistry() {
        if (serviceRegistryMap == null) {
            //provide default implementation:
            serviceRegistryMap = new HashMap<>();
            serviceRegistryMap.put(ContentFileService.class, new ContentFileServiceImpl());
            serviceRegistryMap.put(FileSystemService. class, new FileSystemServiceImpl());
        }
        return serviceRegistryMap;
    }

}
