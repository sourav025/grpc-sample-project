package com.srv.grpc.sample.services;

import com.srv.grpc.sample.chat.ChatMessage;
import com.srv.grpc.sample.chat.ChatMessageFromServer;
import com.srv.grpc.sample.chat.ChatServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.LinkedHashSet;
import java.util.Set;

public class ChatServiceImpl extends ChatServiceGrpc.ChatServiceImplBase {
    private final Set<StreamObserver<ChatMessageFromServer>> observers = new LinkedHashSet<>();

    @Override
    public StreamObserver<ChatMessage> chat(StreamObserver<ChatMessageFromServer> responseObserver) {
        observers.add(responseObserver);

        return new StreamObserver<ChatMessage>() {
            @Override
            public void onNext(ChatMessage chatMessage) {
                // receiving data from from client
                ChatMessageFromServer msg = ChatMessageFromServer.newBuilder()
                        .setMessage(chatMessage)
                        .build();
                observers.forEach(connection -> {
                    connection.onNext(msg);
                });
            }

            @Override
            public void onError(Throwable throwable) {
                observers.remove(responseObserver);
            }

            @Override
            public void onCompleted() {
                observers.remove(responseObserver);
            }
        };
    }
}
