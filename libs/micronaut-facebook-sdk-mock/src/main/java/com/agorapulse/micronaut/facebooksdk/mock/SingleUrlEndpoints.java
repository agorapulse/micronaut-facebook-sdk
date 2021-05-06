package com.agorapulse.micronaut.facebooksdk.mock;

import com.restfb.FacebookEndpoints;

public class SingleUrlEndpoints implements FacebookEndpoints {

    private final String url;

    public SingleUrlEndpoints(String url) {
        this.url = url;
    }

    @Override
    public String getFacebookEndpoint() {
        return url;
    }

    @Override
    public String getGraphEndpoint() {
        return url;
    }

    @Override
    public String getGraphVideoEndpoint() {
        return url;
    }

}
