package com.github.tcurrie.rest.factory.it;

import com.github.tcurrie.rest.factory.Cache;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

final class WebDriverCache extends Cache<WebDriver> {
    private static final int CACHE_SIZE = 3;
    private static final WebDriverCache INSTANCE = WebDriverCache.create();
    private static synchronized WebDriverCache create() {
        return new WebDriverCache();
    }
    static WebDriverCache getInstance() {
        return INSTANCE;
    }

    private WebDriverCache() {
        super(CACHE_SIZE, HtmlUnitDriver::new, WebDriver::quit);
    }
}
