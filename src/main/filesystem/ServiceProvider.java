package filesystem;

import java.util.Map;
import java.util.HashMap;

import static filesystem.constants.Constants.EXISTING_IMPLEMENTATION_COULD_NOT_BE_OVERRIDDEN_MESSAGE;
import static filesystem.constants.Constants.SERVICE_NOT_REGISTERED_MESSAGE;

public class ServiceProvider {

    private final static Map<Class<?>, Object> DEFAULT_IMPLEMENTATION = Map.of(
            ContentFileService.class, new ContentFileServiceImpl(),
            FileSystemService. class, new FileSystemServiceImpl()
    );

    private ServiceProvider(){}

    private static Map<Class<?>, Object> serviceRegistryMap;

    public static  <T> void register(Class<T> serviceType, T serviceInstance) {
        if (serviceRegistryMap != null && getServiceRegistry().containsKey(serviceType)) {
            //only register new impl if there is none for this class
            throw new RuntimeException(String.format(EXISTING_IMPLEMENTATION_COULD_NOT_BE_OVERRIDDEN_MESSAGE, serviceType.getName()));
        }
        getServiceRegistry().put(serviceType, serviceInstance);
    }

    public static <T> T getInstance(Class<T> serviceType) {
        Object serviceInstance = getServiceRegistry().get(serviceType);
        if (serviceInstance == null) {
            throw new RuntimeException(String.format(SERVICE_NOT_REGISTERED_MESSAGE, serviceType.getName()));
        }
        return serviceType.cast(serviceInstance);
    }

    private static Map<Class<?>, Object> getServiceRegistry() {
        if (serviceRegistryMap == null) {
            //provide default implementation:
            serviceRegistryMap = new HashMap<>(DEFAULT_IMPLEMENTATION);
        }
        return serviceRegistryMap;
    }

}
