package ca.lotlinx.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

public class ValueService implements Service<JsonArray, List<String>> {

    ValueService() {
    }

    public JsonArray submit(List<String> images) {
        getLogger().info("Creating JSON object with images.");
        //image
        JsonArray imageSet = new JsonArray();
        int i = 0;
        for (String image : images) {
            JsonObject img = new JsonObject();
            img.addProperty("imageId", i++);
            img.addProperty("imageUrl", image);
            imageSet.add(img);
        }

        //vehicleImage
        JsonArray vehicleImageSets = new JsonArray();
        JsonObject vehicleImg = new JsonObject();
        vehicleImg.addProperty("id", 0);
        vehicleImg.add("imageSet", imageSet);
        vehicleImageSets.add(vehicleImg);

        //dealer
        JsonArray dealers = new JsonArray();
        JsonObject dealer = new JsonObject();
        dealer.addProperty("dealerId", "20182018");
        dealer.add("vehicleImageSets", vehicleImageSets);
        dealers.add(dealer);

        return dealers;
    }
}