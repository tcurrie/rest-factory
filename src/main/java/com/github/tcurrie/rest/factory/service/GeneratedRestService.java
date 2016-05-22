package com.github.tcurrie.rest.factory.service;

import com.github.tcurrie.rest.factory.RestResponseAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public final class GeneratedRestService extends HttpServletBasis {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratedRestService.class);

    private UriSetRestHandlerDictionary configuration;

    protected void service(final HttpServletRequest request, final HttpServletResponse response) {
        try {
            final String result = MethodDelegate.delegate(configuration, request, response);
            writeResult(response, result, HttpServletResponse.SC_OK);
        } catch (final Throwable t) {
            final String result = RestResponseAdaptor.Service.THROWABLE.apply(t);
            writeResult(response, result, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    void writeResult(final HttpServletResponse resp, final String result, final int status)  {
        resp.setStatus(status);
        resp.setHeader("Content-Type", "application/json");
        writeResult(resp, result);
    }

    @Override
    public void init() throws ServletException {
        LOGGER.info("Beginning Servlet Config.");
        final WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        final Map<String, Object> beans = applicationContext.getBeansWithAnnotation(RestService.class);
        LOGGER.info("Got Rest Service Beans[{}]", beans);
        this.configuration = UriSetRestHandlerDictionary.create(RestMethodFactory.create(beans.values()));
        LOGGER.info("Mapped rest services [{}]", configuration);
    }
}
