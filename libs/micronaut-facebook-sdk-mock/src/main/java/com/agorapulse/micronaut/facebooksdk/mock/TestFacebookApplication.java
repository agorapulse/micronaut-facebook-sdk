package com.agorapulse.micronaut.facebooksdk.mock;

import com.agorapulse.micronaut.facebooksdk.DefaultFacebookApplication;
import com.agorapulse.micronaut.facebooksdk.FacebookApplication;
import com.agorapulse.micronaut.facebooksdk.FacebookApplicationConfiguration;
import com.restfb.DefaultFacebookClient;
import com.restfb.DefaultJsonMapper;
import com.restfb.DefaultWebRequestor;
import com.restfb.FacebookClient;
import com.restfb.JsonMapper;
import com.restfb.Version;
import com.restfb.WebRequestor;
import com.stehno.ersatz.ContentType;
import com.stehno.ersatz.ErsatzServer;
import com.stehno.ersatz.Expectations;
import io.micronaut.context.annotation.Replaces;

import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import java.io.Closeable;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.undertow.util.QueryParameterUtils.parseQueryString;

@Singleton
@Replaces(DefaultFacebookApplication.class)
public class TestFacebookApplication implements FacebookApplication, Closeable {

    public static final String TOKEN = "TOKEN";

    private static final Set<String> OVERRIDES = Collections.singleton("https://graph.facebook.com");

    private ErsatzServer server;

    private final DefaultFacebookApplication fallback;
    private final FacebookApplicationConfiguration configuration;

    public TestFacebookApplication(Optional<FacebookApplicationConfiguration> configuration) {
        this.configuration = configuration.orElseGet(FacebookApplicationConfiguration::new);
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
        if (!server.verify()) {
            throw new IllegalStateException("The server verification failed");
        }
        server.stop();
    }

    private static ErsatzServer prepareServer() {
        ErsatzServer server = new ErsatzServer();
        server.reportToConsole(true);
        server.https(true);
        server.decoder(ContentType.APPLICATION_URLENCODED, (body, ctx) -> {
            if (body == null || body.length == 0) {
                return null;
            }
            return parseQueryString(new String(body, StandardCharsets.UTF_8), StandardCharsets.UTF_8.name());
        });
        return server;
    }

}
