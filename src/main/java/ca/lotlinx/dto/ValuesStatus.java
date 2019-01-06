package ca.lotlinx.dto;

public class ValuesStatus implements Base {

    private final Status values;

    public ValuesStatus(Status values) {
        this.values = values;
    }

    public Status getValues() {
        return values;
    }
}