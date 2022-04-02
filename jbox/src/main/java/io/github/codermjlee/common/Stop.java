package io.github.codermjlee.common;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Stop<T> {
    private T data;

    public static <T> Stop<T> create() {
        return new Stop<>();
    }

    public static <T> Stop<T> create(T data) {
        Stop<T> stop = new Stop<>();
        stop.setData(data);
        return stop;
    }
}