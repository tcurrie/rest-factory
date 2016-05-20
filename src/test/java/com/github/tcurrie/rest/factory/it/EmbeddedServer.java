package com.github.tcurrie.rest.factory.it;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class EmbeddedServer {
    private final Tomcat tomcat;

    public static EmbeddedServer create(final int port) {
        return new EmbeddedServer(port);
    }

    private EmbeddedServer(final int port) {
        tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.setBaseDir(createTempDir().toString());
    }


    public void deploy(final String war, final String contextPath) {
        try {
            tomcat.addWebapp(contextPath, (new File(war)).getAbsolutePath());
        } catch (final ServletException e) {
            throw new RuntimeException(e);
        }
    }

    public EmbeddedServer start() {
        try {
            tomcat.start();
        } catch (final LifecycleException e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    public EmbeddedServer stop() {
        try {
            tomcat.stop();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private Path createTempDir() {
        try {
            final Path path = Files.createTempDirectory("EmbeddedServer");
            path.toFile().deleteOnExit();
            return path;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
