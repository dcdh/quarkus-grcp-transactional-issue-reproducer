package org.acme;

import com.google.protobuf.Any;
import com.google.rpc.Code;
import com.google.rpc.ErrorInfo;
import com.google.rpc.Status;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import jakarta.transaction.*;

import java.util.Objects;

@GrpcService
public class DefaultHelloGrpcService extends HelloGrpcGrpc.HelloGrpcImplBase {
    private final UserTransaction transaction;

    public DefaultHelloGrpcService(final UserTransaction transaction) {
        this.transaction = Objects.requireNonNull(transaction);
    }

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        try {
            transaction.begin();
            responseObserver.onError(StatusProto.toStatusRuntimeException(
                    Status.newBuilder()
                            .setCode(Code.NOT_FOUND.getNumber())
                            .addDetails(Any.pack(ErrorInfo.newBuilder()
                                    .setReason("Unknown reason")
                                    .setDomain("org.acme")
                                    .putMetadata("metadataOne", "One")
                                    .putMetadata("metadataTwo", "Two")
                                    .build()))
                            .setMessage(("Unknown message"))
                            .build()));
            transaction.commit();
        } catch (final Exception exception) {
            try {
                transaction.rollback();
            } catch (final SystemException e) {
                throw new RuntimeException(e);
            }
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void sayHelloStream(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        try {
            transaction.begin();
            responseObserver.onError(StatusProto.toStatusRuntimeException(
                    Status.newBuilder()
                            .setCode(Code.NOT_FOUND.getNumber())
                            .addDetails(Any.pack(ErrorInfo.newBuilder()
                                    .setReason("Unknown reason")
                                    .setDomain("org.acme")
                                    .putMetadata("metadataOne", "One")
                                    .putMetadata("metadataTwo", "Two")
                                    .build()))
                            .setMessage(("Unknown message"))
                            .build()));
            transaction.commit();
        } catch (final Exception exception) {
            try {
                transaction.rollback();
            } catch (final SystemException e) {
                throw new RuntimeException(e);
            }
            throw new RuntimeException(exception);
        }
    }
}
