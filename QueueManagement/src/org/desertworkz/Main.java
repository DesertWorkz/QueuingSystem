package org.desertworkz;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class Main {

    public static void main(String[] args) throws Exception {
        // test data
        Handlers.addCounterService("accounts", new String[]{"C1"});
        Handlers.addCounterService("collections", new String[]{"C1", "C2"});
        Handlers.addCounterService("taxation", new String[]{"C2"});
        Handlers.addCounterService("finance", new String[]{"C3"});

        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", 8000), 0);
        server.createContext("/newTicket", new Handlers.newTicket());
        server.createContext("/nextTicket", new Handlers.nextTicket());
        server.setExecutor(null); // creates a default executor
        server.start();
    }
}