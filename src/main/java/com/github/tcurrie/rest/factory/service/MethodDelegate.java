package com.github.tcurrie.rest.factory.service;

import com.github.tcurrie.rest.factory.v1.RestFactoryException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class MethodDelegate {
    private MethodDelegate() {
        throw new RestFactoryException("Can not construct instance of Factory class.");
    }
    private interface Handler {
        String apply(RequestDelegate requestDelegate, HttpServletRequest req, final HttpServletResponse res) throws Throwable;
    }
    @SuppressWarnings("unused")
    private enum Method {
        GET(MethodDelegate::invoke), POST(MethodDelegate::invoke), PUT(MethodDelegate::invoke), DELETE(MethodDelegate::invoke),
        ECHO(MethodDelegate::echo), OPTIONS(MethodDelegate::options);
        private final Handler handler;
        Method(final Handler handler) {
            this.handler = handler;
        }
    }

    private static final String ALLOW = Stream.of(Method.values()).map(Enum::name).collect(Collectors.joining(", "));

    static String delegate(final RequestDelegate delegate, final HttpServletRequest request, final HttpServletResponse response) throws Throwable {
        return Method.valueOf(request.getMethod()).handler.apply(delegate, request, response);
    }

    private static String invoke(final RequestDelegate delegate, final HttpServletRequest request, final HttpServletResponse response) throws Throwable {
        final String body = getBody(request);
        Throwable throwable = new RestFactoryException("Unable to handle request.");
        for (final RestServiceMethod m : delegate.getHandlers(request)) {
            try {
                return m.invoke(body);
            } catch (final Throwable t) {
                throwable = t;
            }
        }
        throw throwable;
    }

    private static String echo(final RequestDelegate delegate, final HttpServletRequest request, final HttpServletResponse response) throws Throwable {
        final String body = getBody(request);
        Throwable throwable = new RestFactoryException("Unable to handle request.");
        for (final RestServiceMethod m : delegate.getHandlers(request)) {
            try {
                return m.echo(body);
            } catch (final Throwable t) {
                throwable = t;
            }
        }
        throw throwable;
    }

    private static String options(final RequestDelegate delegate, final HttpServletRequest request, final HttpServletResponse response) throws Throwable {
        response.setHeader("Allow", ALLOW);
        return "";
    }

    private static String getBody(final HttpServletRequest req) throws IOException {
        return req.getReader().lines().collect(Collectors.joining("\n"));
    }
}
