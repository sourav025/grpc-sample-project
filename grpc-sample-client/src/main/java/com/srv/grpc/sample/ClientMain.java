package com.srv.grpc.sample;

import com.srv.grpc.sample.chat.ChatMessage;
import com.srv.grpc.sample.chat.ChatMessageFromServer;
import com.srv.grpc.sample.chat.ChatServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import javax.swing.*;
import java.util.function.Function;

/**
 * ClientMain
 * Run command : mvn exec:java -Dexec.mainClass=com.srv.grpc.sample.ClientMain
 */
public class ClientMain {
    public static void main(String[] args) {
        String chatUser = args.length > 0 ? args[0] : "User";
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("127.0.0.1", 8080)
                .usePlaintext()
                .build();
        runSampleRequestResponseFlow(channel, chatUser);
        runChatServiceFlow(channel, chatUser);
    }

    private static void runChatServiceFlow(ManagedChannel channel, String name){
        ChatServiceGrpc.ChatServiceStub chatStub = ChatServiceGrpc.newStub(channel);

        StreamObserver<ChatMessage> toServer = chatStub.chat(new StreamObserver<ChatMessageFromServer>() {
            @Override
            public void onNext(ChatMessageFromServer chatMessageFromServer) {
                ChatMessage message = chatMessageFromServer.getMessage();
                System.out.printf("[Received][%s] %s\n", message.getFrom(), message.getMessage());
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                //do nothing
            }
        });

        takeInputFromUser(name, s -> {
            if(s==null) toServer.onCompleted();
            else toServer.onNext(ChatMessage.newBuilder()
                    .setFrom(name)
                    .setMessage(s)
                    .build());
            return null;
        });
    }

    private static void takeInputFromUser(String name, Function<String, String> func){
        new Thread(()->{
            while(true) {
                try {
                    String message = JOptionPane.showInputDialog(String.format("Enter Your message %s", name));
                    func.apply(message);
                }catch (Exception exc) {
                    func.apply(null);
                    return;
                }
            }
        }).start();
    }

    private static void runSampleRequestResponseFlow(ManagedChannel channel, String chatUser) {
        GreetingServiceGrpc.GreetingServiceBlockingStub blockingStub = GreetingServiceGrpc.newBlockingStub(channel);

        GreetResponse response = blockingStub.greet(GreetRequest.newBuilder()
                .setName(chatUser)
                .build());
        System.out.println(response);
    }
}
