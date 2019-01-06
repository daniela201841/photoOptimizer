package ca.lotlinx.dto;

public class Status implements Base {

    private final String token;

    private final String status;

    public Status(String token, String status) {
        this.token = token;
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public String getStatus() {
        return status;
    }
}