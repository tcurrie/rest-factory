package com.github.tcurrie.rest.factory;

import java.lang.reflect.Method;

public class RestUriFactory {
    private static final RestUriFactory INSTANCE = new RestUriFactory();

    public static RestUriFactory getInstance() {
        return INSTANCE;
    }

    private RestUriFactory() {
    }

    public String create(final Class<?> c, final Method m) {
        return "/" + getInterfaceName(c) + "/" + getVersion(c) + "/" + getMethod(m);
    }

    String getInterfaceName(final Class<?> c) {
        return lowerHyphenate(c.getSimpleName());
    }

    String getVersion(final Class<?> c) {
        final String parent = getCanonicalPart(c, -2);
        return parent != null && parent.matches("^[vV][\\d\\.]+$") ? parent.toLowerCase() : "v1";
    }

    String getCanonicalPart(final Class<?> c, final int part) {
        final String[] parts = c.getCanonicalName().split(".");
        return part < parts.length && parts.length + part > -1 ?
                part < 0 ? parts[parts.length + part]
                        : parts[part]
                : null;
    }

    String getMethod(Method m) {
        return lowerHyphenate(m.getName());
    }

    String lowerHyphenate(final String value) {
        return value
                .replaceAll("([^_])_([^_])", "$1-$2")
                .replaceAll("_", "")
                .replaceAll("([a-z0-9])([A-Z])", "$1-$2")
                .toLowerCase();
    }

}
