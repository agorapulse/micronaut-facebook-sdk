/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2019-2023 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
