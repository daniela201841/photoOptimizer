package ca.lotlinx.service.util;

import java.util.ArrayList;
import java.util.Arrays;

public class Headers extends ArrayList<Header> {

    public Headers(Header... headers) {
        Arrays.stream(headers).forEach(this::add);
    }
}
