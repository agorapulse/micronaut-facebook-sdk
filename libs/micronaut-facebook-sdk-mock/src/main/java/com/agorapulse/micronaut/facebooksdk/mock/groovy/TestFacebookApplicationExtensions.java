package com.agorapulse.micronaut.facebooksdk.mock.groovy;

import com.agorapulse.micronaut.facebooksdk.mock.FacebookApiResponse;
import com.agorapulse.micronaut.facebooksdk.mock.TestFacebookApplication;
import com.stehno.ersatz.Expectations;
import com.stehno.ersatz.Request;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
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
