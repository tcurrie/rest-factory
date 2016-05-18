package com.github.tcurrie.rest.factory.it;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;


abstract class WebDriverTestBasis {
    private WebDriver webDriver;

    @Before
    public void before() throws InterruptedException {
        webDriver = WebDriverCache.getInstance().borrow();
    }

    @After
    public void after() {
        WebDriverCache.getInstance().remit(webDriver);
    }

    WebDriver getWebdriver() {
        return webDriver;
    }
}
