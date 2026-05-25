/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2019-2026 Agorapulse.
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

import com.restfb.Connection;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

import java.util.List;

public class FluxConnection {

    private FluxConnection() {
        // disallow instantiation
    }

    public static <T> Flux<List<T>> create(FacebookClient client, String connection, Class<T> connectionType, Parameter... parameters) {
        return Flux.generate(() -> null, (String nextPage, SynchronousSink<List<T>> sink) -> {
            Connection<T> conn = nextPage == null
                    ? client.fetchConnection(connection, connectionType, parameters)
                    : client.fetchConnectionPage(nextPage, connectionType);

            sink.next(conn.getData());

            if (conn.hasNext()) {
                return conn.getNextPageUrl();
            } else {
                sink.complete();
                return null;
            }
        });
    }
}
