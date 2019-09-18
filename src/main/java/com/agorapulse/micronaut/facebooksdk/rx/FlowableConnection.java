package com.agorapulse.micronaut.facebooksdk.rx;

import com.restfb.Connection;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import io.reactivex.Emitter;
import io.reactivex.Flowable;

import java.util.List;

import static io.reactivex.Flowable.generate;

public class FlowableConnection {

    private FlowableConnection() {
        // disallow instantiation
    }

    public static <T> Flowable<List<T>> create(FacebookClient client, String connection, Class<T> connectionType, Parameter... parameters) {
        return generate(() -> null, (String nextPage, Emitter<List<T>> emitter) -> {
            Connection<T> conn = nextPage == null
                    ? client.fetchConnection(connection, connectionType, parameters)
                    : client.fetchConnectionPage(nextPage, connectionType);

            emitter.onNext(conn.getData());

            if (conn.hasNext()) {
                return conn.getNextPageUrl();
            } else {
                emitter.onComplete();
                return null;
            }
        });
    }
}
