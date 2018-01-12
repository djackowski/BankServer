package server;

import datastore.DataStoreManager;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.xml.ws.Endpoint;
import java.io.IOException;
import java.net.URI;

import static datastore.Constants.REST_ADDRESS;

public class Server {
//bin\mongod --dbpath ./data/db/ --port 8004

    public Server() throws IOException {
        initREST();
        initSOAP();

        System.out.println("Started");
    }

    private void initSOAP() {
        Endpoint.publish("http://localhost:8080/users", new UserServiceImpl());
        Endpoint.publish("http://localhost:8080/accounts", new AccountServiceImpl());
        Endpoint.publish("http://localhost:8080/transfers", new TransferServiceImpl());
    }

    private void initREST() throws IOException {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.packages("server");
        resourceConfig.packages("org.glassfish.jersey.examples.jackson")
                .register(JacksonFeature.class);

//        GrizzlyHttpServerFactory.createHttpServer(REST_ADDRESS, resourceConfig).start();
        GrizzlyHttpServerFactory.createHttpServer(URI.create(REST_ADDRESS), resourceConfig).start();



//        NetworkListener networkListener = new NetworkListener("jaxws-listener", "localhost", 8020);
//        HttpServer server = new HttpServer();
//        server.getServerConfiguration().addHttpHandler(new JaxwsHandler(new ExternalTransferResource()), "/");
//        server.addListener(networkListener);
    }


    private static void initMongo() {
        DataStoreManager.initialMongo("localhost", 8004);
    }

    public static void main(String[] args) throws IOException {
        initMongo();
        new Server();
    }
}
