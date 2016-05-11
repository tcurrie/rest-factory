package com.github.tcurrie.rest.factory.it;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RestServiceTestBasis {
    private static final Logger LOGGER = Logger.getLogger(RestServiceTestBasis.class.getName());
    private EmbeddedProxy proxy;
    private WebDriver webdriver;

    @Before
    public void before() {
        proxy = EmbeddedProxy.start(0);
        webdriver = proxy.createDriver();
    }

    @After
    public void after() {
        try {
            webdriver.quit();
        } catch (final Exception e) {
            LOGGER.log(Level.FINEST, "Webdriver failed to quit. {0}", e);
        }
        try {
            proxy.stop();
        } catch (final Exception e) {
            LOGGER.log(Level.FINEST, "Proxy failed to stop. {0}", e);
        }
    }


    protected WebDriver getWebdriver() {
        return webdriver;
    }
}
