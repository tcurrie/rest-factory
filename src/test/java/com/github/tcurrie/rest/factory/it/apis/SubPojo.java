package com.github.tcurrie.rest.factory.it.apis;

import com.openpojo.business.BusinessIdentity;

public class SubPojo extends Pojo {
    protected SubPojo() {
        super();
    }
    public SubPojo(final String value, final int[] data) {
        super(value, data);
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

}
