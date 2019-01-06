package ca.lotlinx.config;

public interface Config {

    default Integer getPort() {
        return 443;
    }

    default String getHost() {
        return "api.lotlinx.com/photoai/v1";
    }

    default String getOptimizeOperation() {
        return "optimize";
    }

    default String getStatusOperation() {
        return "status";
    }

    default String getBasicAuthentication() {
        return "dGVzdGFjY291bnQxMjowZWE1ZTM0NTI2Mzg=";
    }
}