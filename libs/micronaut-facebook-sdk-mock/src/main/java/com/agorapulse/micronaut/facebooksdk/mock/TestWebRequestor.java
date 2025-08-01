/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2019-2025 Agorapulse.
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

import com.restfb.DebugHeaderInfo;
import com.restfb.WebRequestor;

import java.util.Map;

public class TestWebRequestor implements WebRequestor {

    public TestWebRequestor(WebRequestor delegate, Map<String, String> overrides) {
        this.delegate = delegate;
        this.overrides = overrides;
    }

    @Override
    public Response executeGet(Request request) {
        Request testRequest = prepareTestRequest(request);
        try {
            return delegate.executeGet(testRequest);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot GET " + testRequest.getUrl(), e);
        }
    }

    @Override
    public Response executePost(Request request) {
        Request testRequest = prepareTestRequest(request);
        try {
            return delegate.executePost(testRequest);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot POST " + testRequest.getUrl(), e);
        }
    }

    @Override
    public Response executeDelete(Request request) {
        Request testRequest = prepareTestRequest(request);
        try {
            return delegate.executeDelete(testRequest);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot DELETE " + testRequest.getUrl(), e);
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

    private Request prepareTestRequest(Request request) {
        Request testRequest = new Request(
                jailUrl(request.getUrl()),
                request.getHeaderAccessToken(),
                request.getParameters(),
                request.getBinaryAttachments()
        );
        testRequest.setBody(request.getBody());
        return testRequest;
    }

    private final WebRequestor delegate;
    private final Map<String, String> overrides;
}
