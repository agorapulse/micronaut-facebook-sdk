package com.agorapulse.micronaut.facebooksdk.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stehno.ersatz.ContentType;
import com.stehno.ersatz.Response;
import groovy.lang.Closure;

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
