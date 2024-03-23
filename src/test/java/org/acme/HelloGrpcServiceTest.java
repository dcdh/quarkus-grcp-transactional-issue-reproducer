package org.acme;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.List;

import com.google.protobuf.Any;
import com.google.rpc.Code;
import com.google.rpc.ErrorInfo;
import com.google.rpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.test.junit.QuarkusTest;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.AssertSubscriber;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import org.junit.jupiter.api.Test;

@QuarkusTest
class HelloGrpcServiceTest {
    @GrpcClient
    HelloGrpc helloGrpc;

    @Test
    void testHello() {
        Uni<HelloReply> reply = helloGrpc
                .sayHello(HelloRequest.newBuilder().setName("Neo").build());

        final UniAssertSubscriber<HelloReply> subscriber = reply.subscribe()
                .withSubscriber(UniAssertSubscriber.create());
        subscriber
                .awaitFailure(exception -> {
                    final Status status = StatusProto.fromThrowable(exception);
                    assert status != null;
                    final List<Any> detailsList = status.getDetailsList();
                    assertAll(
                            () -> assertThat(exception)
                                    .isInstanceOf(StatusRuntimeException.class)
                                    .hasMessage("NOT_FOUND: Unknown message"),
                            () -> assertThat(status.getCode()).isEqualTo(Code.NOT_FOUND.getNumber()),
                            () -> assertThat(status.getMessage()).isEqualTo("Unknown message"),
                            () -> assertThat(detailsList.size()).isEqualTo(1),
                            () -> assertThat(detailsList.get(0).is(ErrorInfo.class)).isTrue(),
                            () -> assertThat(detailsList.get(0).unpack(ErrorInfo.class).getReason()).isEqualTo("Unknown reason"),
                            () -> assertThat(detailsList.get(0).unpack(ErrorInfo.class).getDomain()).isEqualTo("org.acme"),
                            () -> assertThat(detailsList.get(0).unpack(ErrorInfo.class).getMetadataMap().get("metadataOne")).isEqualTo("One"),
                            () -> assertThat(detailsList.get(0).unpack(ErrorInfo.class).getMetadataMap().get("metadataTwo")).isEqualTo("Two")
                    );
                });
    }

    @Test
    void testHelloStream() {
        Multi<HelloReply> reply = helloGrpc
                .sayHelloStream(HelloRequest.newBuilder().setName("Neo").build());

        final AssertSubscriber<HelloReply> subscriber = reply.subscribe()
                .withSubscriber(AssertSubscriber.create(Integer.MAX_VALUE));
        subscriber
                .awaitFailure(exception -> {
                    final Status status = StatusProto.fromThrowable(exception);
                    assert status != null;
                    final List<Any> detailsList = status.getDetailsList();
                    assertAll(
                            () -> assertThat(exception)
                                    .isInstanceOf(StatusRuntimeException.class)
                                    .hasMessage("NOT_FOUND: Unknown message"),
                            () -> assertThat(status.getCode()).isEqualTo(Code.NOT_FOUND.getNumber()),
                            () -> assertThat(status.getMessage()).isEqualTo("Unknown message"),
                            () -> assertThat(detailsList.size()).isEqualTo(1),
                            () -> assertThat(detailsList.get(0).is(ErrorInfo.class)).isTrue(),
                            () -> assertThat(detailsList.get(0).unpack(ErrorInfo.class).getReason()).isEqualTo("Unknown reason"),
                            () -> assertThat(detailsList.get(0).unpack(ErrorInfo.class).getDomain()).isEqualTo("org.acme"),
                            () -> assertThat(detailsList.get(0).unpack(ErrorInfo.class).getMetadataMap().get("metadataOne")).isEqualTo("One"),
                            () -> assertThat(detailsList.get(0).unpack(ErrorInfo.class).getMetadataMap().get("metadataTwo")).isEqualTo("Two")
                    );
                });
    }
}
