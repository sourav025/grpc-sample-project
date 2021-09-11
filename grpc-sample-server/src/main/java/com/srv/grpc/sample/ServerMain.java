package com.srv.grpc.sample;

import com.srv.grpc.sample.services.ChatServiceImpl;
import com.srv.grpc.sample.services.GreetingServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

/**
 * Run Command: mvn exec:java -Dexec.mainClass=com.srv.grpc.sample.ServerMain
 */
public class ServerMain {
    public static void main(String[] args) throws InterruptedException, IOException {
        Server server = ServerBuilder.forPort(8080)
                .addService(new GreetingServiceImpl())
                .addService(new ChatServiceImpl())
                .build();
        server.start();
        System.out.println("Server started on PORT 8080. Waiting to accept request...");
        server.awaitTermination();
    }
}
