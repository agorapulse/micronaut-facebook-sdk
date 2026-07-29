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
package com.agorapulse.micronaut.facebooksdk;

import com.restfb.FacebookClient;
import com.restfb.exception.FacebookException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

/**
 * Wraps a {@link FacebookClient} so that any {@link FacebookException} it throws no longer carries the request
 * metadata restfb attaches to it.
 *
 * <p>restfb populates every {@code FacebookException} with an {@code InfoData} holding the full request URL —
 * including {@code access_token} and {@code appsecret_proof} — which {@code getMessage()}/{@code toString()} then
 * append. Logging the exception (or persisting its message) would otherwise leak those secrets into logs, Sentry and
 * user-facing errors. Clearing the {@code InfoData} neutralises the leak at the source while keeping the Facebook
 * error description (type, code, subcode) intact for diagnostics.</p>
 */
public final class SecretSafeFacebookClient {

    private SecretSafeFacebookClient() {
    }

    public static FacebookClient wrap(FacebookClient delegate) {
        return (FacebookClient) Proxy.newProxyInstance(
                SecretSafeFacebookClient.class.getClassLoader(),
                new Class<?>[]{FacebookClient.class},
                (proxy, method, args) -> {
                    try {
                        return method.invoke(delegate, args);
                    } catch (InvocationTargetException e) {
                        Throwable cause = e.getCause();
                        throw cause != null ? stripSecrets(cause) : e;
                    }
                });
    }

    private static Throwable stripSecrets(Throwable thrown) {
        if (thrown instanceof FacebookException facebookException) {
            facebookException.withInfoData((FacebookException.InfoData) null);
        }
        return thrown;
    }
}
