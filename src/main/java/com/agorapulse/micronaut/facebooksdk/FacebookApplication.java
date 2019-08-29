package com.agorapulse.micronaut.facebooksdk;

import com.agorapulse.micronaut.facebooksdk.fsr.FacebookSignedRequest;
import com.restfb.FacebookClient;
import com.restfb.Version;

public interface FacebookApplication {

    /**
     * Creates a Facebook Graph API client with API version from the configuration..
     */
    default FacebookClient createClient() {
        return createClient(getConfiguration().getApiVersion());
    }

    /**
     * @param accessToken A Facebook OAuth access token.
     * @return a Facebook Graph API client with API version from the configuration..
     */
    default FacebookClient createClient(String accessToken) {
        return createClient(accessToken, getConfiguration().getSecret(), getConfiguration().getApiVersion());
    }

    /**
     * @param apiVersion Version of the api endpoint
     * @return a Facebook Graph API client with the given {@code apiVersion}.
     */
    default FacebookClient createClient(Version apiVersion) {
        return createClient(null, getConfiguration().getSecret(), apiVersion);
    }

    /**
     * @param accessToken A Facebook OAuth access token.
     * @param apiVersion  Version of the api endpoint
     * @return a Facebook Graph API client with the given {@code accessToken}.
     */
    default FacebookClient createClient(String accessToken, Version apiVersion) {
        return createClient(accessToken, getConfiguration().getSecret(), apiVersion);
    }

    /**
     * @param accessToken A Facebook OAuth access token.
     * @param appSecret   A Facebook application secret.
     * @param apiVersion  Version of the api endpoint
     * @return a Facebook Graph API client with the given {@code accessToken}.
     */
    FacebookClient createClient(String accessToken, String appSecret, Version apiVersion);

    /**
     * @return application configuration
     */
    FacebookApplicationConfiguration getConfiguration();

    /**
     * @param signature encoded signed request (param or cookie)
     * @return parsed Facebook signed request
     */
    default FacebookSignedRequest parseSignedRequest(String signature) {
        return FacebookSignedRequest.parse(getConfiguration().getSecret(), signature);
    }

}
