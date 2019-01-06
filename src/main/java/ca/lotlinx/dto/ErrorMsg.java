package ca.lotlinx.dto;

public class ErrorMsg implements Base {

    private final String errorMsg;

    public ErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}