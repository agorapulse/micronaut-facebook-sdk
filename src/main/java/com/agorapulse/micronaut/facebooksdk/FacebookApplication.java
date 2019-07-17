package com.agorapulse.micronaut.facebooksdk;

import com.agorapulse.micronaut.facebooksdk.fsr.FacebookSignedRequest;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;

public class FacebookApplication {

    private final FacebookApplicationConfiguration configuration;

    public FacebookApplication(FacebookApplicationConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Creates a Facebook Graph API client with API version from the configuration..
     */
    public FacebookClient createClient() {
        return createClient(configuration.getApiVersion());
    }

    /**
     * @param accessToken A Facebook OAuth access token.
     * @return a Facebook Graph API client with API version from the configuration..
     */
    public FacebookClient createClient(String accessToken) {
        return createClient(accessToken, configuration.getSecret(), configuration.getApiVersion());
    }

    /**
     * @param apiVersion Version of the api endpoint
     * @return a Facebook Graph API client with the given {@code apiVersion}.
     */
    public FacebookClient createClient(Version apiVersion) {
        return createClient(null, configuration.getSecret(), apiVersion);
    }

    /**
     * @param accessToken A Facebook OAuth access token.
     * @param apiVersion  Version of the api endpoint
     * @return a Facebook Graph API client with the given {@code accessToken}.
     */
    public FacebookClient createClient(String accessToken, Version apiVersion) {
        return createClient(accessToken, configuration.getSecret(), apiVersion);
    }

    /**
     * @param accessToken A Facebook OAuth access token.
     * @param appSecret   A Facebook application secret.
     * @param apiVersion  Version of the api endpoint
     * @return a Facebook Graph API client with the given {@code accessToken}.
     */
    public FacebookClient createClient(String accessToken, String appSecret, Version apiVersion) {
        return new DefaultFacebookClient(accessToken, appSecret, apiVersion);
    }

    /**
     * @return application configuration
     */
    public FacebookApplicationConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * @param signature encoded signed request (param or cookie)
     * @return parsed Facebook signed request
     */
    public FacebookSignedRequest parseSignedRequest(String signature) {
        return FacebookSignedRequest.parse(getConfiguration().getSecret(), signature);
    }

    @Override
    public String toString() {
        return "FacebookApplication[id:" + configuration.getId() + "]";
    }
}
