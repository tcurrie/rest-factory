package com.github.tcurrie.rest.factory.service;

import com.github.tcurrie.rest.factory.Strings;
import com.github.tcurrie.rest.factory.v1.RestFactoryException;
import com.github.tcurrie.rest.factory.v1.RestMethod;
import com.github.tcurrie.rest.factory.v1.RestMethodDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class UriSetRestHandlerDictionary implements RequestDelegate {
    private static final Logger LOGGER = LoggerFactory.getLogger(UriSetRestHandlerDictionary.class);
    private final Set<RestServiceMethod> handlers;
    private final ThreadLocal<String> servletUrl;

    static UriSetRestHandlerDictionary create(final Stream<RestServiceMethod> handlers) {
        final UriSetRestHandlerDictionary d = new UriSetRestHandlerDictionary();
        handlers.forEach(h -> {
            if (d.handlers.contains(h)) {
                @SuppressWarnings("OptionalGetWithoutIsPresent") final RestServiceMethod match = d.handlers.stream().filter(h::equals).findFirst().get();
                throw new RestFactoryException(Strings.format("Illegal method overloading [{}] vs [{}].  Rest method will not accurately reflect the compile time behavior.", match, h));
            }
            d.handlers.add(h);
        });
        d.handlers.addAll(RestMethodFactory.create((RestMethodDictionary) d::getHandlerDescription).collect(Collectors.toSet()));
        return d;
    }

    private UriSetRestHandlerDictionary() {
        //noinspection unchecked
        this.handlers = new TreeSet<>((a, b)->a.getImplementation().compareTo(b.getImplementation()));
        this.servletUrl = new ThreadLocal<>();
    }

    private Set<RestMethod> getHandlerDescription() {
        return handlers.stream().map(h -> RestMethod.create(servletUrl.get() + h.getUri(), h.getImplementation().getApi())).collect(Collectors.toSet());
    }

    @Override
    public List<RestServiceMethod> getHandlers(final HttpServletRequest req) {
        final String uri = req.getRequestURI();

        String servletUri = req.getContextPath() + req.getServletPath();
        String servletURL = req.getRequestURL().substring(0, req.getRequestURL().indexOf(servletUri) + servletUri.length());
        this.servletUrl.set(servletURL);

        final List<RestServiceMethod> handler = find(uri);
        if (handler.size() == 0) {
            LOGGER.warn("Failed to match request [{}] to any Handler from [{}]", req.getRequestURI(), handlers);
            throw new RestFactoryException(Strings.format("Failed to match request [{}] to any Handler from [{}]", req.getRequestURI(), getHandlerDescription()));
        }
        return handler;
    }

    private List<RestServiceMethod> find(final String uri) {
        return handlers.stream().filter(h -> uri.contains(h.getUri())).collect(Collectors.toList());
    }
}
