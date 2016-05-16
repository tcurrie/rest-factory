package com.github.tcurrie.rest.factory.it;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WebDriverTestBasis {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverTestBasis.class);
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
            LOGGER.debug("Webdriver failed to quit.", e);
        }
    }

    protected WebDriver getWebdriver() {
        return webdriver;
    }
}
