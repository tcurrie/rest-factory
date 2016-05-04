package simple.rest.factory;

import java.lang.reflect.Method;

public class RestUriFactory {
    public static String create(final Class<?> c, final Method m) {
        return "/" + getInterfaceName(c) + "/" + getVersion(c) + "/" + getMethod(m);
    }

    static String getInterfaceName(final Class<?> c) {
        return lowerHyphenate(c.getSimpleName());
    }

    static String getVersion(final Class<?> c) {
        final String parent = getCanonicalPart(c, -2);
        return parent != null && parent.matches("^[vV][\\d\\.]+$") ? parent.toLowerCase() : "v1";
    }

    static String getCanonicalPart(final Class<?> c, final int part) {
        final String[] parts = c.getCanonicalName().split(".");
        return part < parts.length && parts.length + part > -1 ?
                part < 0 ? parts[parts.length + part]
                        : parts[part]
                : null;
    }

    static String getMethod(Method m) {
        return lowerHyphenate(m.getName());
    }

    static String lowerHyphenate(final String value) {
        return value
                .replaceAll("([^_])_([^_])", "$1-$2")
                .replaceAll("_", "")
                .replaceAll("([a-z0-9])([A-Z])", "$1-$2")
                .toLowerCase();
    }

}
