package com.github.tcurrie.rest.factory.it.apis;

import com.openpojo.business.BusinessIdentity;
import com.openpojo.business.annotation.BusinessKey;

import java.util.Arrays;

public class SuperPojo {
    @BusinessKey(required = false)
    private String value;

    @BusinessKey(required = false)
    private int[] data;

    @SuppressWarnings("unused")
    protected SuperPojo() {
    }

    public SuperPojo(final String value, final int[] data) {
        this.value = value;
        this.data = data;
    }

    public String getValue() {
        return value;
    }

    public int[] getData() {
        return data;
    }


    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object o) {
        return BusinessIdentity.areEqual(this, o);

    }

    @Override
    public int hashCode() {
        return BusinessIdentity.getHashCode(this);
    }

    @Override
    public String toString() {
        return "Pojo{" +
                "value='" + value + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }

}
