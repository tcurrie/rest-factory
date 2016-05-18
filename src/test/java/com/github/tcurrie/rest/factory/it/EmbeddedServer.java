package com.github.tcurrie.rest.factory.it;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.ServletException;
import java.io.File;

public class EmbeddedServer {
    private final Tomcat tomcat;

    public EmbeddedServer(final int port) {
        tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.setBaseDir(getTempBaseDir().getAbsolutePath());
    }

    public static EmbeddedServer on(final int port) {
        return new EmbeddedServer(port);
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

    private File getTempBaseDir() {
        return createTempDirectory("tomcat");
    }

    private static final int TEMP_DIR_ATTEMPTS = 100;

    private File createTempDirectory(final String prefix) {
        final File baseDir = new File(System.getProperty("java.io.tmpdir"));
        final String baseName = prefix + "-" + System.nanoTime() + "-";

        for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
            final File tempDir = new File(baseDir, baseName + counter);
            if (tempDir.mkdir()) {
                return tempDir;
            }
        }
        throw new IllegalStateException("Failed to create directory within "
                + TEMP_DIR_ATTEMPTS + " attempts (tried "
                + baseName + "0 to " + baseName + (TEMP_DIR_ATTEMPTS - 1) + ')');
    }
}
