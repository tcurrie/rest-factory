package com.github.tcurrie.rest.factory.it;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.filters.RequestFilter;
import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.Consumer;

public class EmbeddedProxy {
    private final BrowserMobProxy proxy;
    private final String address = "localhost";

    public EmbeddedProxy(final int port) {
        proxy = new BrowserMobProxyServer();
        proxy.start(port, getLocalHostInetAddress());
    }

    private InetAddress getLocalHostInetAddress() {
        try {
            return InetAddress.getByName(address);
        } catch (final UnknownHostException uhe) {
            throw new RuntimeException(uhe);
        }
    }

    public static EmbeddedProxy start(final int port) {
        return new EmbeddedProxy(port);
    }

    public WebDriver createDriver() {
        final HtmlUnitDriver htmlUnitDriver = new HtmlUnitDriver();
        htmlUnitDriver.setProxy(address, proxy.getPort());
        return htmlUnitDriver;
    }

    public void filterRequest(final Consumer<HttpRequest> o) {
        proxy.addRequestFilter(new RequestFilter() {
            @Override
            public HttpResponse filterRequest(final HttpRequest request, final HttpMessageContents contents,
                    final HttpMessageInfo messageInfo) {
                o.accept(request);
                return null;
            }
        });
    }

    public void stop() {
        proxy.stop();
    }
}
