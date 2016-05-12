package com.github.tcurrie.rest.factory.service;

import com.github.tcurrie.rest.factory.RestFactoryException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class GeneratedRestService extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(GeneratedRestService.class.getName());
    private UriSetRestHandlerDictionary configuration;

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) {
        configuration.getHandler(req).invoke(req, resp);
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) {
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
            super.service(req, resp);
        }
    }

    @Override
    public void init() throws ServletException {
        try {
            LOGGER.info("Beginning Servlet Config.");
            final WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
            final Map<String, Object> beans = applicationContext.getBeansWithAnnotation(RestService.class);
            LOGGER.log(Level.INFO, "Got Rest Service Beans[{0}]", beans);
            this.configuration = UriSetRestHandlerDictionary.create(RestMethodFactory.create(beans.values()));
            LOGGER.log(Level.INFO, "Mapped rest services [{0}]", configuration);
        } catch (final Exception e) {
            throw RestFactoryException.create(LOGGER, "Failed to map Generated Rest Services.", e);
        }
    }
}
