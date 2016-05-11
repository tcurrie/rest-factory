package com.github.tcurrie.rest.factory.it.apis;

import java.io.Serializable;
import java.util.Arrays;

public class Pojo implements Serializable {
    private String value;
    private int[] data;

    private Pojo() {
    }

    public Pojo(final String value, final int[] data) {
        this.value = value;
        this.data = data;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public int[] getData() {
        return data;
    }

    public void setData(final int[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Pojo{" +
                "value='" + value + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
