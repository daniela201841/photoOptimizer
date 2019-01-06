package ca.lotlinx.dto;

public class StatusError implements Base {

    private final ErrorMsg meta;

    private final Integer status;

    public StatusError(ErrorMsg meta, Integer status) {
        this.meta = meta;
        this.status = status;
    }

    public ErrorMsg getMeta() {
        return meta;
    }

    public Integer getStatus() {
        return status;
    }
}