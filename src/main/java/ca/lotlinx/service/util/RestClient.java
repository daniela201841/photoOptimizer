package ca.lotlinx.service.util;

import com.google.gson.JsonElement;

import java.util.List;

public interface RestClient {

    JsonElement post(JsonElement element, String commandUrl, List<Param> params, Headers headers);

    JsonElement get(String commandUrl, List<Param> params, Headers headers);
}
