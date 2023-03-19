package app;

import app.controller.CatalogController;
import app.dao.CatalogDao;
import io.reactivex.netty.protocol.http.server.HttpServer;

public class Application {
    private static final int PORT = 8080;
    private static final CatalogController controller =
            new CatalogController(new CatalogDao());

    public static void main(String[] args) {
        HttpServer
                .newServer(PORT)
                .start((request, response) ->
                        response.writeString(controller.process(request))
                ).awaitShutdown();
    }
}
