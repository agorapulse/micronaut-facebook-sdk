/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2019-2025 Agorapulse.
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
package com.agorapulse.micronaut.facebooksdk.mock;

import com.agorapulse.micronaut.facebooksdk.DefaultFacebookApplication;
import com.agorapulse.micronaut.facebooksdk.FacebookApplication;
import com.agorapulse.micronaut.facebooksdk.FacebookApplicationConfiguration;
import com.agorapulse.micronaut.facebooksdk.FacebookSdkFactory;
import com.restfb.DefaultFacebookClient;
import com.restfb.DefaultJsonMapper;
import com.restfb.DefaultWebRequestor;
import com.restfb.FacebookClient;
import com.restfb.JsonMapper;
import com.restfb.Version;
import com.restfb.WebRequestor;
import io.github.cjstehno.ersatz.ErsatzServer;
import io.github.cjstehno.ersatz.cfg.ContentType;
import io.github.cjstehno.ersatz.cfg.Expectations;
import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Replaces;

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.exceptions.BeanInstantiationException;
import io.micronaut.runtime.context.scope.Refreshable;
import io.micronaut.runtime.context.scope.refresh.RefreshEvent;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.undertow.util.QueryParameterUtils.parseQueryString;

@Singleton
@Refreshable
@Replaces(value = DefaultFacebookApplication.class, factory = FacebookSdkFactory.class)
public class TestFacebookApplication implements FacebookApplication, Closeable, ApplicationEventListener<RefreshEvent> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TestFacebookApplication.class);

    public static final String TOKEN = "TOKEN";

    private static final Set<String> OVERRIDES = Collections.singleton("https://graph.facebook.com");

    private ErsatzServer server;

    private final DefaultFacebookApplication fallback;
    private final FacebookApplicationConfiguration configuration;

    public TestFacebookApplication(BeanContext context) {
        FacebookApplicationConfiguration maybeConfiguration = null;
        try {
            maybeConfiguration = context.containsBean(FacebookApplicationConfiguration.class) ? context.getBean(FacebookApplicationConfiguration.class) : new FacebookApplicationConfiguration();
        } catch (BeanInstantiationException ex){
            LOGGER.trace("FacebookApplicationConfiguration not found, using default configuration", ex);
        }
        this.configuration = maybeConfiguration == null ? new FacebookApplicationConfiguration() : maybeConfiguration;
        this.fallback = new DefaultFacebookApplication(this.configuration);

    }

    @Override
    public FacebookClient createClient(String accessToken, String appSecret, Version apiVersion) {
        if (server == null) {
            // mockApi hasn't been called
            return fallback.createClient(accessToken, appSecret, apiVersion);
        }

        server.start();

        WebRequestor requestor = new TestWebRequestor(new DefaultWebRequestor(), OVERRIDES.stream().collect(Collectors.toMap(Function.identity(), it -> server.getHttpUrl())));
        JsonMapper mapper = new DefaultJsonMapper();

        DefaultFacebookClient client = new DefaultFacebookClient(accessToken, appSecret, requestor, mapper, apiVersion);

        client.setFacebookEndpointUrls(new SingleUrlEndpoints(server.getHttpUrl()));

        return client;
    }

    public void mockApi(Consumer<Expectations> expectations) {
        if (server == null) {
            server = prepareServer();
        }
        server.expectations(expectations);
    }

    @Override
    public FacebookApplicationConfiguration getConfiguration() {
        return configuration;
    }

    @PreDestroy
    public void close() {
        if (server == null) {
            // mockApi hasn't been called
            return;
        }

        if (!server.verify()) {
            throw new IllegalStateException("The server verification failed");
        }
        server.stop();
    }

    @Override
    public void onApplicationEvent(RefreshEvent event) {
        close();
    }

    private static ErsatzServer prepareServer() {
        ErsatzServer server = new ErsatzServer(serverConfig -> {
            serverConfig.reportToConsole(true);
            serverConfig.https(true);
            serverConfig.decoder(ContentType.APPLICATION_URLENCODED, (body, ctx) -> {
                if (body == null || body.length == 0) {
                    return null;
                }
                return parseQueryString(new String(body, StandardCharsets.UTF_8), StandardCharsets.UTF_8.name());
            });
        });

        return server;
    }

}
