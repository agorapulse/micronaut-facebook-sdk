package com.agorapulse.micronaut.facebooksdk.mock;

import com.restfb.BinaryAttachment;
import com.restfb.DebugHeaderInfo;
import com.restfb.WebRequestor;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TestWebRequestor implements WebRequestor {

    public TestWebRequestor(WebRequestor delegate, Map<String, String> overrides) {
        this.delegate = delegate;
        this.overrides = overrides;
    }

    @Override
    public Response executeGet(String url, String headerAccessToken) {
        try {
            return delegate.executeGet(jailUrl(url), headerAccessToken);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot GET " + url, e);
        }
    }

    @Override
    public Response executeGet(String url) throws IOException {
        return executeGet(url, null);
    }

    @Override
    public Response executePost(String url, String parameters, String headerAccessToken) {
        try {
            return delegate.executePost(jailUrl(url), parameters, headerAccessToken);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot POST " + url, e);
        }
    }

    @Override
    public Response executePost(String url, String parameters, List<BinaryAttachment> binaryAttachments, String headerAccessToken) {
        try {
            return delegate.executePost(jailUrl(url), parameters, binaryAttachments, headerAccessToken);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot POST " + url, e);
        }
    }

    @Override
    public Response executeDelete(String url, String headerAccessToken) {
        try {
            return delegate.executeDelete(jailUrl(url), headerAccessToken);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot DELETE " + url, e);
        }
    }

    @Override
    public DebugHeaderInfo getDebugHeaderInfo() {
        return delegate.getDebugHeaderInfo();
    }

    private String jailUrl(String url) {
        String result = url;

        for(Map.Entry<String, String> e : overrides.entrySet()) {
            result = result.replace(e.getKey(), e.getValue());
        }

        return result;
    }

    private final WebRequestor delegate;
    private final Map<String, String> overrides;
}
