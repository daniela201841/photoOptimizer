package ca.lotlinx.service;

import ca.lotlinx.config.Config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceRegistry {

    private static Map<ServiceType, Service> registry = new ConcurrentHashMap<>();

    static {
        Config config = new Config() {
            // default values
        };

        registry.put(ServiceType.VALUE_SERVICE, new ValueService());

        registry.put(ServiceType.REST_SERVICE, new RestService(config));

        registry.put(ServiceType.SUBMIT_IMAGE_SERVICE, new SubmitImageService(
                (ValueService) registry.get(ServiceType.VALUE_SERVICE),
                (RestService) registry.get(ServiceType.REST_SERVICE),
                config));
    }

    private ServiceRegistry() {
    }

    public static Service getServiceRegistry(ServiceType type) {
        return registry.get(type);
    }
}
