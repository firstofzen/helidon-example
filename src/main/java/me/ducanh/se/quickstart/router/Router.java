package me.ducanh.se.quickstart.router;

import io.helidon.config.Config;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;
import io.helidon.webserver.websocket.WsRouting;
import me.ducanh.se.quickstart.handler.rest.GreetService;
import me.ducanh.se.quickstart.handler.ws.Ws;

public class Router {

    public static void init(Config config) {
        WebServer server = WebServer.builder()
                .config(config.get("server"))
                .routing(Router::routing)
                .addRouting(WsRouting.builder().endpoint("/ws", new Ws()))
                .build()
                .start();
        System.out.println("WEB server is up! http://localhost:" + server.port());
    }

    public static void routing(HttpRouting.Builder routing) {
        routing
                .register("/greet", new GreetService())
                .get("/simple-greet", (req, res) -> res.send("Hello World!"));
    }

}
