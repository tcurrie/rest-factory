package com.github.tcurrie.rest.factory.client;

import com.github.tcurrie.rest.factory.Strings;
import com.github.tcurrie.rest.factory.v1.RestFactoryException;
import com.github.tcurrie.rest.factory.v1.TimeOut;
import com.openpojo.reflection.PojoField;
import com.openpojo.reflection.impl.PojoClassFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class HTTPExchange {
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private static final PojoField CONNECTION_METHOD = PojoClassFactory.getPojoClass(HttpURLConnection.class).getPojoFields().stream().filter(f -> f.getName().equals("method")).findFirst().get();

    private HTTPExchange() {
        throw new RestFactoryException("Can not construct instance of Factory class.");
    }

    public enum Method {
        GET, POST, PUT, DELETE, ECHO
    }

    public static String execute(final String url, final String body, final Method method, final TimeOut timeout) {
        return TimeBoxed.attempt(() -> complete(url, body, method, adapt(timeout)), timeout);
    }

    private static int adapt(final TimeOut timeout) {
        return (int) TimeOut.convert(timeout, TimeUnit.MILLISECONDS).getTime();
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

            /**
             *  In order to override the HttpURLConnection method type constraints, the field value must be set directly
             *  and it must be done after the connection output stream is used.
             *  TODO Add test for this behavior
             */
            CONNECTION_METHOD.set(connection, method.name());

            // TODO break this handler pattern out and make it so that server errors get error stream and anything else wraps the bad result in a unigue exception. (versus a null pointer)
            final InputStream is = connection.getResponseCode() == HttpServletResponse.SC_OK ? connection.getInputStream() : connection.getErrorStream();

            try (final BufferedReader in = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return in.lines().collect(Collectors.joining("\n"));
            }
        } catch (final Exception e) {
            throw new RestFactoryException(Strings.format("Failed to execute HTTPExchange, url[{}], body[{}], method[{}], timeout[{}].", url, body, method, timeout), e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
