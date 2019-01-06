package ca.lotlinx.service;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SubmitImageServiceTest {

    @Test
    public void submitImage() {
        try (Stream<String> stream = Files.lines(Paths.get("/home/dfvp/Public/LotLinx/input.txt"))) {
            List<String> files = stream.collect(Collectors.toList());
            Service<Boolean, List<String>> submitImageService = ServiceRegistry.getServiceRegistry(ServiceType.SUBMIT_IMAGE_SERVICE);
            Boolean b = submitImageService.submit(files);

            Assert.assertThat(Objects.isNull(b), CoreMatchers.is(false));
            Assert.assertThat(Boolean.TRUE.equals(b), CoreMatchers.is(true));
            Assert.assertThat(Boolean.FALSE.equals(b), CoreMatchers.is(false));
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
