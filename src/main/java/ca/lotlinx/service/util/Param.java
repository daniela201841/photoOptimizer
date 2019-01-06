package ca.lotlinx.service.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Logger;

public class Param {

    public final String name;

    public final String value;

    public Param(String name, String value) {
        String encoded = "";

        try {
            encoded = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getAnonymousLogger().severe(ex.getLocalizedMessage());
        }

        this.name = name;
        this.value = encoded;
    }
}
