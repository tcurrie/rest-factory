package com.github.tcurrie.rest.factory.it;

public enum RestServers {
    SERVER("/spring");
    private final String contextPath;

    RestServers(final String contextPath) {
        RewriteServer.SERVER.deploy(RewriteServer.WAR, contextPath);
        this.contextPath = contextPath;
    }
    public String getUrl() {
        return RewriteServer.BASE_URL + contextPath;
    }
    private interface RewriteServer {
         int PORT = Integer.parseInt(System.getProperty("test.port", "9090"));
         String BASE_URL = System.getProperty("test.host", "http://localhost:" + PORT);
         String WAR = System.getProperty("test.war", RewriteServer.class.getResource("/webapp/").getPath());
         EmbeddedServer SERVER = EmbeddedServer.on(PORT);
    }
    static {
        RewriteServer.SERVER.start();
        Runtime.getRuntime().addShutdownHook(new ServerShutdownHook());
    }

    private static class ServerShutdownHook extends Thread {
        @Override
        public void run() {
            RewriteServer.SERVER.stop();
        }
    }
}
