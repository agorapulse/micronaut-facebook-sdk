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
package com.agorapulse.micronaut.facebooksdk.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stehno.ersatz.ContentType;
import com.stehno.ersatz.Response;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class FacebookApiResponse {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    public static final String DEFAULT_ERROR_TYPE = "OAuthException";

    public static Consumer<Response> just(String json) {
        return r -> r.body(json, ContentType.APPLICATION_JSON);
    }

    public static Consumer<Response> json(Object object) {
        return r -> r.body(toJson(Objects.requireNonNull(object, "Object returned from facebook cannot be null!")), ContentType.APPLICATION_JSON);
    }

    public static Consumer<Response> emptyData() {
        return json(Collections.singletonMap("data", Collections.emptyList()));
    }

    public static Consumer<Response> successful() {
        return json(Collections.singletonMap("success", true));
    }

    public static Consumer<Response> unsuccessful() {
        return json(Collections.singletonMap("success", false));
    }

    public static Consumer<Response> id(String id) {
        return json(Collections.singletonMap("id", id));
    }

    public static Consumer<Response> error(int code, int subcode, String message) {
        return error(code, subcode, message, DEFAULT_ERROR_TYPE);
    }

    public static Consumer<Response> error(int code, String message) {
        return error(code, message, DEFAULT_ERROR_TYPE);
    }

    public static Consumer<Response> error(int code, String message, String type) {
        return error(code, 0, message, type);
    }

    public static Consumer<Response> error(int code, int subcode, String message, String type) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("code", code);
        if (subcode != 0) {
            error.put("subcode", subcode);
        }
        error.put("fbtrace_id", "Ep7tdv7lPCC");
        error.put("message", message);
        error.put("type", type);

        return json(Collections.singletonMap("error", error));
    }

    private static String toJson(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Cannot convert " + object + " to JSON");
        }
    }
}
