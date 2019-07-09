package com.agorapulse.micronaut.facebooksdk;

import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Factory;

@Factory
public class FacebookSdkFactory {

    @EachBean(FacebookApplicationConfiguration.class)
    public FacebookApplication facebookApplication(FacebookApplicationConfiguration configuration) {
        return new FacebookApplication(configuration);
    }

}
