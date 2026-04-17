package pocketbudget;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import pocketbudget.api.ConfigurationServerRest;

import java.net.URL;

public class PocketBudgetMain {

    public static void main(String[] args) throws Exception {
        int port = getPort();
        Server server = new Server(port);

        // ── 1. Jersey: serves all /api/** requests ──────────────────────────
        ServletContextHandler apiHandler = new ServletContextHandler();
        apiHandler.setContextPath("/api");
        ServletHolder jerseyServlet = new ServletHolder(
                new ServletContainer(new ConfigurationServerRest()));
        jerseyServlet.setInitOrder(0);
        apiHandler.addServlet(jerseyServlet, "/*");

        // ── 2. Static files: React frontend from classpath /static ──────────
        ResourceHandler staticHandler = new ResourceHandler();
        staticHandler.setWelcomeFiles(new String[]{"index.html"});
        staticHandler.setDirectoriesListed(false);
        URL staticUrl = PocketBudgetMain.class.getClassLoader().getResource("static");
        if (staticUrl != null) {
            staticHandler.setResourceBase(staticUrl.toExternalForm());
        }

        // ── 3. Combine handlers ─────────────────────────────────────────────
        HandlerList handlers = new HandlerList();
        handlers.addHandler(apiHandler);
        handlers.addHandler(staticHandler);
        server.setHandler(handlers);

        server.start();
        System.out.println("PocketBudget running on http://localhost:" + port);
        server.join();
    }

    private static int getPort() {
        String env = System.getenv("PORT");
        return (env != null && !env.isBlank()) ? Integer.parseInt(env) : 8080;
    }
}
