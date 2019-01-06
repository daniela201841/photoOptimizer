package ca.lotlinx.service;

import java.util.logging.Logger;

public interface Service<R, P> {

    R submit(P param);

    default Logger getLogger() {
        return Logger.getLogger(this.getClass().getName());
    }
}
