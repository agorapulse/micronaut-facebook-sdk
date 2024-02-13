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
package com.agorapulse.micronaut.facebooksdk.fsr;

import com.agorapulse.micronaut.facebooksdk.FacebookApplication;
import com.agorapulse.micronaut.facebooksdk.FacebookApplicationConfiguration;
import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.bind.binders.TypedRequestArgumentBinder;

import jakarta.inject.Singleton;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.of;

@Singleton
public class FacebookSignedRequestBinder implements TypedRequestArgumentBinder<FacebookSignedRequest> {

    private static final String SIGNED_REQUEST_PARAMETER_NAME = "signed_request";
    private static final String COOKIE_PREFIX = "fbsr_";

    private final FacebookApplication application;
    private final FacebookApplicationConfiguration configuration;

    public FacebookSignedRequestBinder(FacebookApplication application, FacebookApplicationConfiguration configuration) {
        this.application = application;
        this.configuration = configuration;
    }

    @Override
    public Argument<FacebookSignedRequest> argumentType() {
        return Argument.of(FacebookSignedRequest.class);
    }

    @Override
    public BindingResult<FacebookSignedRequest> bind(ArgumentConversionContext<FacebookSignedRequest> context, HttpRequest<?> source) {
        FacebookApplicationConfiguration conf = application.getConfiguration();

        Optional<String> requestParameter = source.getParameters().get(SIGNED_REQUEST_PARAMETER_NAME, String.class);
        if (requestParameter.isPresent()) {
            return () -> of(FacebookSignedRequest.parse(configuration.getSecret(), requestParameter.get()));
        }

        Optional<?> body = source.getBody();
        if (body.isPresent() && body.get() instanceof Map) {
            Map params = (Map) body.get();
            Object bodyParameter = params.get(SIGNED_REQUEST_PARAMETER_NAME);
            if (bodyParameter != null) {
                return () -> of(FacebookSignedRequest.parse(configuration.getSecret(), bodyParameter.toString()));
            }
        }

        return () -> source.getCookies().get(COOKIE_PREFIX + conf.getId(), String.class).map(sr -> FacebookSignedRequest.parse(conf.getSecret(), sr));
    }
}
