package com.github.tcurrie.rest.factory.v1;

import com.openpojo.business.BusinessIdentity;
import com.openpojo.business.annotation.BusinessKey;

public final class RestMethod {
    @BusinessKey
    private String uri;
    @BusinessKey
    private String api;

    public static RestMethod create(final String uri, final String api) {
        return new RestMethod(uri, api);
    }

    @SuppressWarnings("unused")
    private RestMethod() {}

    private RestMethod(final String uri, final String api) {
        this.uri = uri;
        this.api = api;
    }

    public String getUri() {
        return uri;
    }

    public String getApi() {
        return api;
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
