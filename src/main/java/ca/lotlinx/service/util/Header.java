package ca.lotlinx.service.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Logger;

public class Header {

    private static final String UTF = "UTF-8";

    public final String name;

    public final String value;

    public Header(String name, String value, boolean encode) {
        String encoded = value;
        if (encode) {
            try {
                encoded = URLEncoder.encode(value, UTF);
            } catch (UnsupportedEncodingException ex) {
                Logger.getAnonymousLogger().severe(ex.getLocalizedMessage());
            }
        }

        this.name = name;
        this.value = encoded;
    }
}
