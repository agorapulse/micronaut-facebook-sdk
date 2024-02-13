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
package com.agorapulse.micronaut.facebooksdk.mock.groovy;

import com.agorapulse.micronaut.facebooksdk.mock.FacebookApiResponse;
import com.agorapulse.micronaut.facebooksdk.mock.TestFacebookApplication;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import io.github.cjstehno.ersatz.cfg.Expectations;
import io.github.cjstehno.ersatz.cfg.Request;
import space.jasan.support.groovy.closure.ConsumerWithDelegate;

import static groovy.lang.Closure.DELEGATE_FIRST;

public class TestFacebookApplicationExtensions {

    public static Request just(Request request, String json) {
        return request.responder(FacebookApiResponse.just(json));
    }

    public static Request json(Request request, Object object) {
        return request.responder(FacebookApiResponse.json(object));
    }

    public static Request emptyData(Request request) {
        return request.responder(FacebookApiResponse.emptyData());
    }

    public static Request successful(Request request) {
        return request.responder(FacebookApiResponse.successful());
    }

    public static Request unsuccessful(Request request) {
        return request.responder(FacebookApiResponse.unsuccessful());
    }

    public static Request id(Request request, String id) {
        return request.responder(FacebookApiResponse.id(id));
    }

    public static Request error(Request request, int code, int subcode, String message) {
        return request.responder(FacebookApiResponse.error(code, subcode, message));
    }

    public static Request error(Request request, int code, String message) {
        return request.responder(FacebookApiResponse.error(code, message));
    }

    public static Request error(Request request, int code, String message, String type) {
        return request.responder(FacebookApiResponse.error(code, message, type));
    }

    public static Request error(Request request, int code, int subcode, String message, String type) {
        return request.responder(FacebookApiResponse.error(code, subcode, message, type));
    }

    public static void mockApi(
            TestFacebookApplication facebookApplication,
            @ClosureParams(value = SimpleType.class, options = "com.stehno.ersatz.Expectations")
            @DelegatesTo(value = Expectations.class, strategy = DELEGATE_FIRST)
                    Closure<?> expectations
    ) {
        facebookApplication.mockApi(ConsumerWithDelegate.create(expectations));
    }

}
