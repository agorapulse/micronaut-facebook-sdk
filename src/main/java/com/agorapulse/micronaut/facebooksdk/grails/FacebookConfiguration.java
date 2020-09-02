package com.agorapulse.micronaut.facebooksdk.grails;

import com.agorapulse.micronaut.facebooksdk.FacebookApplication;
import com.agorapulse.micronaut.grails.MicronautBeanImporter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FacebookConfiguration {

    @Bean
    public MicronautBeanImporter facebookSdkImporter() {
        return MicronautBeanImporter.create().addByType(FacebookApplication.class);
    }

}
