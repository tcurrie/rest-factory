package com.github.tcurrie.rest.factory.client;

import com.github.tcurrie.rest.factory.Strings;
import com.github.tcurrie.rest.factory.v1.RestFactoryException;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoField;
import com.openpojo.reflection.impl.PojoClassFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class HTTPExchange {
    private HTTPExchange() {
        throw RestFactoryException.create("Can not construct instance of Factory class.");
    }

    public enum Method {
        GET, POST, PUT, DELETE, ECHO
    }

    public static String execute(final String url, final String body, final Method method, final int timeout, final TimeUnit timeUnit) {
        return TimeBoxed.attempt(() -> complete(url, body, method, convert(timeout, timeUnit)), timeout, timeUnit);
    }

    private static int convert(final int timeout, final TimeUnit timeUnit) {
        return (int) TimeUnit.MILLISECONDS.convert(timeout, timeUnit);
    }

    private static String complete(final String url, final String body, final Method method, final int timeout) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();

            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length", Integer.toString(body.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            try (final DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
                out.writeBytes(body);
            }

            PojoClass pc = PojoClassFactory.getPojoClass(HttpURLConnection.class);
            PojoField pf = pc.getPojoFields().stream().filter(f->f.getName().equals("method")).findFirst().get();
            pf.set(connection, method.name());

            try (final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                return in.lines().collect(Collectors.joining("\n"));
            }
        } catch (final Exception e) {
            throw RestFactoryException.create(Strings.format("Failed to execute HTTPExchange, url[{}], body[{}], method[{}], timeout[{}].", url, body, method, timeout), e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
