package org.acme;

import com.google.protobuf.Any;
import com.google.rpc.Code;
import com.google.rpc.ErrorInfo;
import com.google.rpc.Status;
import io.grpc.protobuf.StatusProto;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.transaction.Transactional;

//@GrpcService
public class MutinyHelloGrpcService implements HelloGrpc {

    @Override
    @Transactional
    public Uni<HelloReply> sayHello(HelloRequest request) {
        return Uni.createFrom()
                .failure(StatusProto.toStatusRuntimeException(
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
    }

    @Override
    @Transactional
    public Multi<HelloReply> sayHelloStream(HelloRequest request) {
        return Multi.createFrom()
                .failure(StatusProto.toStatusRuntimeException(
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
    }
}
