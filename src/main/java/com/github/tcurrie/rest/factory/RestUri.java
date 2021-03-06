package com.github.tcurrie.rest.factory;

import com.openpojo.business.BusinessIdentity;
import com.openpojo.business.annotation.BusinessKey;

import java.util.function.Supplier;

public class RestUri implements Supplier<String> {
    private final Supplier<String> urlSupplier;
    @BusinessKey private final String methodUri;

    public static RestUri create(final Supplier<String> urlSupplier, final String methodUri) {
        return new RestUri(urlSupplier, methodUri);
    }

    private RestUri(final Supplier<String> urlSupplier, final String methodUri) {
        this.urlSupplier = urlSupplier;
        this.methodUri = methodUri;
    }

    @SuppressWarnings("unused")
    public Supplier<String> getUrlSupplier() {
        return urlSupplier;
    }

    @SuppressWarnings("unused")
    public String getMethodUri() {
        return methodUri;
    }

    @Override
    public String get() {
        return urlSupplier.get() + methodUri;
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
