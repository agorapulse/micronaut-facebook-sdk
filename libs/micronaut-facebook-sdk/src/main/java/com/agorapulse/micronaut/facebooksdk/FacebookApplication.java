/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2021 Agorapulse.
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

}
