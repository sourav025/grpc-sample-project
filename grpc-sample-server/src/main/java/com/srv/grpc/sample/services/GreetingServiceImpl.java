package com.srv.grpc.sample.services;

import com.srv.grpc.sample.GreetRequest;
import com.srv.grpc.sample.GreetResponse;
import com.srv.grpc.sample.GreetingServiceGrpc;
import io.grpc.stub.StreamObserver;

public class GreetingServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {
    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        String name = request.getName();
        responseObserver.onNext(GreetResponse.newBuilder()
                .setMessage(String.format("Greet [%s] from GRPC Server", name))
                .build()
        );
        responseObserver.onCompleted();
    }
}
