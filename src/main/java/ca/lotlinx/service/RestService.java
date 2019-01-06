package ca.lotlinx.service;

import ca.lotlinx.config.Config;
import ca.lotlinx.dto.ErrorMsg;
import ca.lotlinx.dto.GenericReturn;
import ca.lotlinx.dto.Status;
import ca.lotlinx.dto.StatusError;
import ca.lotlinx.dto.StatusType;
import ca.lotlinx.dto.ValuesStatus;
import ca.lotlinx.service.util.Header;
import ca.lotlinx.service.util.Headers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class RestService implements Service<GenericReturn, JsonArray> {

    private final Gson gson;

    private final Config config;

    RestService(Config config) {
        this.config = config;
        this.gson = new GsonBuilder().create();
    }

    /**
     * Post to send images to Server for processing
     *
     * @param jsonArray
     * @return GenericReturn
     */
    public GenericReturn postOptimize(JsonArray jsonArray) {
        getLogger().info("Sending a request to optimize image.");

        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            SimpleRestClient simpleClient = new SimpleRestClient(SimpleRestClient.Scheme.HTTPS,
                                                                 config.getHost(),
                                                                 config.getPort(),
                                                                 client);
            JsonObject object = new JsonObject();
            object.add("values", jsonArray);

            JsonElement response = simpleClient.post(object,
                                                     config.getOptimizeOperation(),
                                                     new ArrayList<>(),
                                                     buildHeaders(config.getBasicAuthentication()));

            if (response.toString().contains("values")) {
                GenericReturn<ValuesStatus> objectReturned = new GenericReturn<ValuesStatus>();
                JsonObject data = gson.fromJson(response, JsonObject.class);
                JsonArray valuesArray = data.getAsJsonArray("values");
                String tk;
                String st;
                for (JsonElement v : valuesArray) {
                    tk = v.getAsJsonObject().get("token").getAsString();
                    st = v.getAsJsonObject().get("status").getAsString();
                    Status ss = new Status(tk, st);
                    ValuesStatus vs = new ValuesStatus(ss);
                    objectReturned.set(vs);

                    return objectReturned;
                }
            } else {
                GenericReturn<StatusError> objectReturned = new GenericReturn<StatusError>();
                JsonObject data = gson.fromJson(response, JsonObject.class);
                JsonArray valuesArray = data.getAsJsonArray("");
                for (JsonElement v : valuesArray) {
                    ErrorMsg em = new ErrorMsg(v.getAsJsonObject().get("meta").getAsJsonObject().get("errorMsg").getAsString());
                    StatusError s = new StatusError(em, Integer.parseInt(v.getAsJsonObject().get("status").getAsString()));
                    objectReturned.set(s);

                    return objectReturned;
                }
            }

        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Request to optimize image failed.", e);
        }

        return null;
    }

    /**
     * Verify status of the processing
     *
     * @param token: identifier of the processing
     * @return
     */
    public boolean verifyStatus(String token) {
        getLogger().info("Checking status token: " + token);

        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            SimpleRestClient simpleClient = new SimpleRestClient(SimpleRestClient.Scheme.HTTPS,
                                                                 config.getHost(),
                                                                 config.getPort(),
                                                                 client);

            JsonElement response = simpleClient.get(config.getOptimizeOperation() + "/" + token + "/" + config.getStatusOperation(),
                                                    new ArrayList<>(),
                                                    buildHeaders(config.getBasicAuthentication()));

            JsonObject data = gson.fromJson(response, JsonObject.class);
            JsonArray valuesArray = data.getAsJsonArray("values");
            for (JsonElement v : valuesArray) {
                return (StatusType.COMPLETE.equals(v.getAsJsonObject().get("status").getAsString()));
            }
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Check status failed: " + token, e);
        }

        return false;
    }

    /**
     * Load method to get modified Urls after complete processing
     *
     * @param token: identifier of the processing
     * @return list of modifieds urls
     */
    public List<String> load(String token) {
        List<String> urls = new ArrayList<>();

        getLogger().info("Loading token: " + token);

        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            SimpleRestClient simpleClient = new SimpleRestClient(SimpleRestClient.Scheme.HTTPS,
                                                                 config.getHost(),
                                                                 config.getPort(),
                                                                 client);

            JsonElement response = simpleClient.get(config.getOptimizeOperation() + "/" + token,
                                                    new ArrayList<>(),
                                                    buildHeaders(config.getBasicAuthentication()));

            JsonObject data = gson.fromJson(response, JsonObject.class);
            JsonArray valuesArray = data.getAsJsonArray("values");
            for (JsonElement v : valuesArray) {
                JsonArray ovis = v.getAsJsonObject().getAsJsonArray("optimizedVehicleImageSets");
                for (JsonElement vis : ovis) {
                    JsonArray is = vis.getAsJsonObject().getAsJsonArray("optimizedImageSet");
                    for (JsonElement oi : is) {
                        urls.add(oi.getAsJsonObject().get("modifiedUrl").getAsString());
                    }
                }
            }

        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Load token failed: " + token, e);
        }

        return urls;
    }

    @Override
    public GenericReturn submit(JsonArray param) {
        throw new RuntimeException("Submit not allowed.");
    }

    public static Headers buildHeaders(String accessToken) {
        return new Headers(new Header("Authorization", String.format("Basic %s", accessToken), false));
    }
}