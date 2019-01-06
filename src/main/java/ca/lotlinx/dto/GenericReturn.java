package ca.lotlinx.dto;

public class GenericReturn<T> {

    private T t;

    public void set(T t) {
        this.t = t;
    }

    public T get() {
        return t;
    }
}
