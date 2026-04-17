package pocketbudget;

import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import pocketbudget.api.ConfigurationServerRest;

import java.net.URI;

public class PocketBudgetMain {
    public static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        URI baseUri = URI.create("http://localhost:" + PORT + "/");
        ResourceConfig config = new ConfigurationServerRest();
        var server = JettyHttpContainerFactory.createServer(baseUri, config);
        server.start();
        System.out.println("PocketBudget API running at http://localhost:" + PORT);
        server.join();
    }
}
