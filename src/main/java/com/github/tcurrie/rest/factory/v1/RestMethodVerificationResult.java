package com.github.tcurrie.rest.factory.v1;

import com.github.tcurrie.rest.factory.proxy.Methods;
import com.openpojo.business.BusinessIdentity;
import com.openpojo.business.annotation.BusinessKey;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RestMethodVerificationResult {
    private static final Predicate<RestMethodVerificationResult> FAILED_OR_INVALID = r -> !r.isSuccess() || !Arrays.deepEquals(r.getArgs(), r.getResult());
    @BusinessKey private String url;
    @BusinessKey private String api;
    private Object[] args;
    protected Object[] result;
    protected Throwable exception;
    private boolean success;

    public static RestMethodVerificationResult createSuccess(final String url, final String api, final Object[] args, final Object[] result) {
        final RestMethodVerificationResult method = new RestMethodVerificationResult(url, api, args);
        method.result = result;
        method.success = true;
        return method;
    }

    public static RestMethodVerificationResult createFailure(final String url, final String api, final Object[] args, final Throwable throwable) {
        final RestMethodVerificationResult method = new RestMethodVerificationResult(url, api, args);
        method.exception = throwable;
        method.success = false;
        return method;
    }

    @SuppressWarnings("WeakerAccess")
    public static Set<RestMethodVerificationResult> getErrorsAndMissing(final Set<RestMethodVerificationResult> results, final Class<?>... types) {
        return Arrays.stream(types).flatMap(t -> Methods.TypeFactory.stream(t).map(m -> {
                    final String api = m.toGenericString();
                    final Set<RestMethodVerificationResult> matched = results.stream().filter(r->r.getApi().equals(api)).collect(Collectors.toSet());

                    if (matched.isEmpty()) {
                        return Stream.of(RestMethodVerificationResult.createFailure("???", api, null, new RestFactoryException("Failed to find result.")));
                    } else {
                        return matched.stream().filter(FAILED_OR_INVALID);
                    }
                }
        ).flatMap(s->s)).collect(Collectors.toSet());
    }

    @SuppressWarnings("unused")
    private RestMethodVerificationResult() {}
    private RestMethodVerificationResult(final String url, final String api, final Object[] args) {
        this.url = url;
        this.api = api;
        this.args = args;
    }

    public String getUrl() {
        return url;
    }

    public String getApi() {
        return api;
    }

    public Object[] getArgs() {
        return args;
    }

    public Object[] getResult() {
        return result;
    }

    public Throwable getException() {
        return exception;
    }

    public boolean isSuccess() {
        return success;
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
