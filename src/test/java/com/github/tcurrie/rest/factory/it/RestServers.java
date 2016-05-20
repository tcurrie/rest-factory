package com.github.tcurrie.rest.factory.it;

public enum RestServers {
    SERVER("/spring");
    private final String contextPath;

    RestServers(final String contextPath) {
        Server.SERVER.deploy(Server.WAR, contextPath);
        this.contextPath = contextPath;
    }

    public String getUrl() {
        return Server.BASE_URL + contextPath;
    }

    private interface Server {
         int PORT = Integer.parseInt(System.getProperty("test.port", "9090"));
         String BASE_URL = System.getProperty("test.host", "http://localhost:" + PORT);
         String WAR = System.getProperty("test.war", Server.class.getResource("/webapp/").getPath());
         EmbeddedServer SERVER = EmbeddedServer.create(PORT);
    }

    static {
        Server.SERVER.start();
        Runtime.getRuntime().addShutdownHook(new ServerShutdownHook());
    }

    private static class ServerShutdownHook extends Thread {
        @Override
        public void run() {
            Server.SERVER.stop();
        }
    }
}
