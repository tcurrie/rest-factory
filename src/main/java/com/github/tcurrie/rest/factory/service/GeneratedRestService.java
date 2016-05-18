package com.github.tcurrie.rest.factory.service;

import com.github.tcurrie.rest.factory.v1.RestFactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

public final class GeneratedRestService extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratedRestService.class);
    private UriSetRestHandlerDictionary configuration;

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) {
        System.out.println("\n\n\nDO GET\n\n\n");
        configuration.getHandler(req).invoke(req, resp);
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) {
        System.out.println("\n\n\nDO POST\n\n\n");

        configuration.getHandler(req).invoke(req, resp);
    }

    @Override
    protected void doPut(final HttpServletRequest req, final HttpServletResponse resp) {
        configuration.getHandler(req).invoke(req, resp);
    }

    @Override
    protected void doDelete(final HttpServletRequest req, final HttpServletResponse resp) {
        configuration.getHandler(req).invoke(req, resp);
    }

    protected void doEcho(final HttpServletRequest req, final HttpServletResponse resp) {
        configuration.getHandler(req).echo(req, resp);
    }

    @Override
    protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        if("ECHO".equals(req.getMethod())) {
            doEcho(req, resp);
        } else {
            System.out.println(req.getMethod());
            Enumeration<String> headers = req.getHeaderNames();
            while (headers.hasMoreElements()) {
                String name = headers.nextElement();
                String value = req.getHeader(name);
                System.out.println(name + "=" + value);
            }
            super.service(req, resp);
        }
    }

    @Override
    public void init() throws ServletException {
        try {
            LOGGER.info("Beginning Servlet Config.");
            final WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
            final Map<String, Object> beans = applicationContext.getBeansWithAnnotation(RestService.class);
            LOGGER.info("Got Rest Service Beans[{}]", beans);
            this.configuration = UriSetRestHandlerDictionary.create(RestMethodFactory.create(beans.values()));
            LOGGER.info("Mapped rest services [{}]", configuration);
        } catch (final Exception e) {
            LOGGER.error("Failed to map Generated Rest Services.", e);
            throw RestFactoryException.create("Failed to map Generated Rest Services.", e);
        }
    }
}
