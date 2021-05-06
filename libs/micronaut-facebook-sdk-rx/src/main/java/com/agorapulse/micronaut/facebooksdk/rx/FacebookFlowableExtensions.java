package com.agorapulse.micronaut.facebooksdk.rx;

import com.restfb.FacebookClient;
import com.restfb.Parameter;
import io.reactivex.Flowable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FacebookFlowableExtensions {


    /**
     * Fetches a Graph API {@code Connection} type, mapping the result to an instance of {@code connectionType}.
     *
     * @param <T>            Java type to map to.
     * @param connection     The name of the connection, e.g. {@code "me/feed"}.
     * @param connectionType Connection type token.
     * @return An instance of type {@code connectionType} which contains the requested Connection's data.
     */
    public static <T> Flowable<List<T>> fetchFlowable(FacebookClient facebookClient, String connection, Class<T> connectionType) {
        return fetchFlowable(facebookClient, connection, connectionType, Collections.emptyMap());
    }

    /**
     * Fetches a Graph API {@code Connection} type, mapping the result to an instance of {@code connectionType}.
     *
     * @param <T>            Java type to map to.
     * @param connection     The name of the connection, e.g. {@code "me/feed"}.
     * @param connectionType Connection type token.
     * @param parameters     URL parameters to include in the API call (optional).
     * @return An instance of type {@code connectionType} which contains the requested Connection's data.
     */
    public static <T> Flowable<List<T>> fetchFlowable(FacebookClient facebookClient, String connection, Class<T> connectionType, Map<String, Object> parameters) {
        return FlowableConnection.create(facebookClient, connection, connectionType, buildVariableArgs(parameters));
    }

    private static Parameter[] buildVariableArgs(Map<String, Object> parameters) {
        return parameters
                .entrySet()
                .stream()
                .map(e -> Parameter.with(e.getKey(), e.getValue()))
                .toArray(Parameter[]::new);
    }

}
