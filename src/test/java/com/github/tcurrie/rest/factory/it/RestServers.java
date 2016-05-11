package com.github.tcurrie.rest.factory.it;

public enum RestServers {
    SERVER("/spring");
    private final String contextPath;

    RestServers(final String contextPath) {
        RewriteServer.server.deploy(RewriteServer.WAR, contextPath);
        this.contextPath = contextPath;
    }
    public String getUrl() {
        return RewriteServer.BASE_URL + contextPath;
    }
    public String getContextPath() {
        return contextPath;
    }
    private static final class RewriteServer {
        private static final int PORT = Integer.parseInt(System.getProperty("test.port", "9090"));
        private static final String BASE_URL = System.getProperty("test.host", "http://localhost:" + PORT);
        private static final String WAR = System.getProperty("test.war", RewriteServer.class.getResource("/webapp/").getPath());
        private static EmbeddedServer server = EmbeddedServer.on(PORT);
    }
    static {
        RewriteServer.server.start();
        Runtime.getRuntime().addShutdownHook(new ServerShutdownHook());
    }

    private static class ServerShutdownHook extends Thread {
        @Override
        public void run() {
            RewriteServer.server.stop();
        }
    }
}
