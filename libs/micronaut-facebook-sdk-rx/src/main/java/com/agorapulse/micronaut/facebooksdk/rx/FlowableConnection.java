/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2022 Agorapulse.
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
