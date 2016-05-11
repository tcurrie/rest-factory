package com.github.tcurrie.rest.factory.it;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;

public class RestServiceTestBasis {
    private EmbeddedProxy proxy;
    private WebDriver webdriver;

    @Before
    public void before() {
        proxy = EmbeddedProxy.start(0);
        webdriver = proxy.createDriver();
    }

    @After
    public void after() {
        webdriver.quit();
        proxy.stop();
    }


    protected WebDriver getWebdriver() {
        return webdriver;
    }
}
