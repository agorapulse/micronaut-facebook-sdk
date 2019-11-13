package com.agorapulse.micronaut.facebooksdk;

import io.micronaut.context.annotation.ConfigurationProperties;

import javax.inject.Named;

@Named("grails")
@ConfigurationProperties("grails.plugin.facebooksdk.app")
public class LegacyFacebookApplicationConfiguration extends AbstractFacebookApplicationConfiguration {

}
