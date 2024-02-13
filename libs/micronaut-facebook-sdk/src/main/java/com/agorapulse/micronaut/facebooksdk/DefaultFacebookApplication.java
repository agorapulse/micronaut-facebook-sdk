/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2019-2024 Agorapulse.
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

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;

public class DefaultFacebookApplication implements FacebookApplication {

    private final FacebookApplicationConfiguration configuration;

    public DefaultFacebookApplication(FacebookApplicationConfiguration configuration) {
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

    @Override
    public String toString() {
        return "FacebookApplication[id:" + configuration.getId() + "]";
    }
}
