package com.agorapulse.micronaut.facebooksdk;

import io.micronaut.context.annotation.ConfigurationProperties;

import javax.inject.Named;

@Named("default")
@ConfigurationProperties("facebook.sdk.app")
public class FacebookApplicationConfiguration extends AbstractFacebookApplicationConfiguration {

}
