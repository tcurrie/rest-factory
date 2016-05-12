package com.github.tcurrie.rest.factory.it;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WebDriverTestBasis {
    private static final Logger LOGGER = Logger.getLogger(WebDriverTestBasis.class.getName());
    private WebDriver webdriver;

    @Before
    public void before() {
        webdriver = new HtmlUnitDriver();
    }

    @After
    public void after() {
        try {
            webdriver.quit();
        } catch (final Exception e) {
            LOGGER.log(Level.FINEST, "Webdriver failed to quit. {0}", e);
        }
    }


    protected WebDriver getWebdriver() {
        return webdriver;
    }
}
