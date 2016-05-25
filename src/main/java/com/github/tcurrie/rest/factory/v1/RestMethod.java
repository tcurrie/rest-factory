package com.github.tcurrie.rest.factory.v1;

import com.openpojo.business.BusinessIdentity;
import com.openpojo.business.annotation.BusinessKey;

public final class RestMethod {
    @BusinessKey
    private String uri;
    private String method;
    private String bean;

    public static RestMethod create(final String uri, final String method, final String bean) {
        return new RestMethod(uri, method, bean);
    }

    @SuppressWarnings("unused")
    private RestMethod() {}

    private RestMethod(final String uri, final String method, final String bean) {
        this.uri = uri;
        this.method = method;
        this.bean = bean;
    }

    public String getUri() {
        return uri;
    }

    public String getMethod() {
        return method;
    }

    public String getBean() {
        return bean;
    }

    @Override
    public int hashCode() {
        return BusinessIdentity.getHashCode(this);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object obj) {
        return BusinessIdentity.areEqual(this, obj);
    }

    @Override
    public String toString() {
        return BusinessIdentity.toString(this);
    }
}
