package ca.lotlinx.service;

import ca.lotlinx.service.util.Headers;
import ca.lotlinx.service.util.Param;
import ca.lotlinx.service.util.RestClient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class SimpleRestClient implements RestClient {

    private final JsonParser parser;

    private final CloseableHttpClient client;

    private final Scheme scheme;

    private final String host;

    private final int port;

    private final String prefix;

    public static enum Scheme {
        HTTP,
        HTTPS;

        private Scheme() {
        }
    }

    public SimpleRestClient(Scheme scheme,
                            String host,
                            int port,
                            CloseableHttpClient client) {

        this.parser = new JsonParser();
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        if (scheme == Scheme.HTTPS && port == 443) {
            this.prefix = scheme.name().toLowerCase(Locale.US) + "://" + host + "/";
        } else {
            this.prefix = scheme.name().toLowerCase(Locale.US) + "://" + host + ":" + port + "/";
        }

        this.client = client;
    }

    public JsonElement get(String path, List<Param> queryParams, Headers headers) {
        HttpGet get = new HttpGet(this.prefix + path + this.queryParamString(queryParams));
        headers.forEach((h) -> {
            get.addHeader(h.name, h.value);
        });
        String result = this.execute(get);
        return result != null ? this.parser.parse(result) : null;
    }

    public JsonElement post(JsonElement body, String path, List<Param> queryParams, Headers headers) {
        HttpEntity encodedBody = new StringEntity(body.toString(), ContentType.APPLICATION_JSON);
        return this.post((HttpEntity) encodedBody, path, queryParams, headers);
    }

    private JsonElement post(HttpEntity body, String path, List<Param> queryParams, Headers headers) {
        HttpPost post = new HttpPost(this.prefix + path + this.queryParamString(queryParams));
        post.setEntity(body);
        headers.forEach((h) -> {
            post.addHeader(h.name, h.value);
        });
        String result = this.execute(post);
        return result != null ? this.parser.parse(result) : null;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            SimpleRestClient that = (SimpleRestClient) o;
            return Objects.equals(this.port, that.port) && Objects.equals(this.scheme, that.scheme) && Objects.equals(this.host, that.host);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.scheme, this.host, this.port});
    }

    private String execute(HttpUriRequest method) {
        return this.executeBody(method);
    }

    private String executeBody(HttpUriRequest method) {
        CloseableHttpResponse response = null;

        String result;
        try {
            HttpClientContext context = HttpClientContext.create();

            response = this.client.execute(method, context);
            int statusCode = response.getStatusLine().getStatusCode();
            String body = response.getEntity() != null ? EntityUtils.toString(response.getEntity(), "UTF-8") : null;
            if (statusCode < 200 || statusCode > 299) {
                Optional<String> type = this.getExceptionType(body);
                throw new RuntimeException(String.format("Error: %s - %s - %s", statusCode, body, type));
            }
            result = body;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            HttpClientUtils.closeQuietly(response);
        }

        return result;
    }

    private Optional<String> getExceptionType(String body) {
        try {
            if (body != null) {
                JsonObject json = this.parser.parse(body).getAsJsonObject();
                String jsonType = json.getAsJsonPrimitive("type").getAsString();

                return Optional.ofNullable(jsonType);
            }
        } catch (Exception ex) {
            ;
        }

        return Optional.empty();
    }

    private String queryParamString(List<Param> parameters) {
        return (String) parameters.stream().map((param) -> {
            return param.name + "=" + param.value;
        }).reduce((s1, s2) -> {
            return s1 + "&" + s2;
        }).map((s) -> {
            return "?" + s;
        }).orElse("");
    }
}