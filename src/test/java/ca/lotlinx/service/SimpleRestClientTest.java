package ca.lotlinx.service;

import ca.lotlinx.service.util.Header;
import ca.lotlinx.service.util.Headers;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public class SimpleRestClientTest {

    @Test
    public void submitImage_optimize_post() {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            SimpleRestClient simpleClient = new SimpleRestClient(SimpleRestClient.Scheme.HTTPS,
                                                                 "api.lotlinx.com/photoai/v1",
                                                                 443,
                                                                 client);

            try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("optimize.json")) {
                String values = createStringFromIS(is);
                Assert.assertThat(Objects.isNull(values), CoreMatchers.is(false));

                JsonParser jsonParser = new JsonParser();
                JsonArray jsonArray = (JsonArray) jsonParser.parse(values);

                JsonObject object = new JsonObject();
                object.add("values", jsonArray);

                JsonElement response = simpleClient.post(object,
                                                         "optimize",
                                                         new ArrayList<>(),
                                                         buildHeaders());

                System.out.println(response);

                // sample: {"values":[{"token":"Nc1OJMMO95r0YrbFouvPwceXEFa2gjYSHJP9NRUdRwT1BoADr8Lnh0xeYFmOVsRT","status":"queued"}]}
                Assert.assertThat(Objects.isNull(response), CoreMatchers.is(false));
                Assert.assertThat(response.toString().contains("token"), CoreMatchers.is(true));
                Assert.assertThat(response.toString().contains("status"), CoreMatchers.is(true));
                Assert.assertThat(response.toString().contains("queue"), CoreMatchers.is(true));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void status_optimize_get() {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            SimpleRestClient simpleClient = new SimpleRestClient(SimpleRestClient.Scheme.HTTPS,
                                                                 "api.lotlinx.com/photoai/v1",
                                                                 443,
                                                                 client);

            JsonElement response = simpleClient.get("optimize",
                                                    new ArrayList<>(),
                                                    buildHeaders());

            System.out.println(response);

            Assert.assertThat(Objects.isNull(response), CoreMatchers.is(false));
            Assert.assertThat(response.toString().contains("status"), CoreMatchers.is(true));

        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void load_optimize_get() {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            SimpleRestClient simpleClient = new SimpleRestClient(SimpleRestClient.Scheme.HTTPS,
                                                                 "api.lotlinx.com/photoai/v1",
                                                                 443,
                                                                 client);

            JsonElement response = simpleClient.get("optimize/14vdb4MqVVidexImjfWMz6UkDsoT8I6ZXaCs7bQXPPXsPRFhfGtgMRqt2JNFVLMO/status",
                                                    new ArrayList<>(),
                                                    buildHeaders());

            System.out.println(response);

            Assert.assertThat(Objects.isNull(response), CoreMatchers.is(false));
            Assert.assertThat(response.toString().contains("status"), CoreMatchers.is(true));

        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
    }

    //https://api.lotlinx.com/photoai/v1/optimize/14vdb4MqVVidexImjfWMz6UkDsoT8I6ZXaCs7bQXPPXsPRFhfGtgMRqt2JNFVLMO

    private static String createStringFromIS(InputStream is) {
        return new BufferedReader(new InputStreamReader(is))
                .lines().collect(Collectors.joining("\n"));
    }

    private static Headers buildHeaders() {
        return new Headers(new Header("Authorization", String.format("Basic %s", "dGVzdGFjY291bnQxMjowZWE1ZTM0NTI2Mzg="), false));
    }
}
