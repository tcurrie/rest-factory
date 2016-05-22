package com.github.tcurrie.rest.factory.service;

import com.github.tcurrie.rest.factory.RestResponseAdaptor;
import com.github.tcurrie.rest.factory.Strings;
import com.github.tcurrie.rest.factory.v1.RestFactoryException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class HttpServletBasis extends GenericServlet {
    @Override
    public void service(final ServletRequest request, final ServletResponse response) throws ServletException, IOException {

        final String protocol = request.getProtocol();
        if (protocol.startsWith("HTTP")) {
            service((HttpServletRequest) request,  (HttpServletResponse) response);
        } else {
            final RestFactoryException e = new RestFactoryException(Strings.format("Invalid request protocol [{}].  Accepts [http].", protocol));
            final String result = RestResponseAdaptor.Service.THROWABLE.apply(e);
            writeResult(response, result);
        }
    }

    protected abstract void service(final HttpServletRequest request, final HttpServletResponse response);


    void writeResult(final ServletResponse response, final String result)  {
        try {
            response.getWriter().write(result);
        } catch (final IOException e) {
            throw new RestFactoryException(Strings.format("Failed to write response [{}].", result), e);
        }
    }
}
