package ca.lotlinx;

import ca.lotlinx.service.Service;
import ca.lotlinx.service.ServiceRegistry;
import ca.lotlinx.service.ServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PhotoOptimizer {
    private static final Logger logger
            = LoggerFactory.getLogger(PhotoOptimizer.class);

    public static void main(String[] args) {
        logger.info("Example log from {}", PhotoOptimizer.class.getSimpleName());

        if (args.length == 0 || Objects.isNull(args[0]) ||  args[0].isEmpty()) {
            System.out.println("Input file not found.");
            System.out.println("Please input the filename");

        } else {
            Path path = Paths.get(args[0]);

            if (Files.isDirectory(path)) {
                System.out.println("Input is a directory. Filename not found.");
                System.out.println("Please input filename");
            }

            try (Stream<String> stream = Files.lines(Paths.get(args[0]))) {
                List<String> files = stream.collect(Collectors.toList());
                Service<Boolean, List<String>> submitImageService = ServiceRegistry.getServiceRegistry(ServiceType.SUBMIT_IMAGE_SERVICE);
                submitImageService.submit(files);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
