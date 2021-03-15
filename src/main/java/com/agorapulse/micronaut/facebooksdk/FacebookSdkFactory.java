package com.agorapulse.micronaut.facebooksdk;

import com.restfb.Version;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.core.convert.TypeConverter;

import javax.inject.Singleton;
import java.util.Optional;

@Factory
public class FacebookSdkFactory {

    @EachBean(FacebookApplicationConfiguration.class)
    public FacebookApplication facebookApplication(FacebookApplicationConfiguration configuration) {
        return new DefaultFacebookApplication(configuration);
    }

    @Bean
    @Singleton
    public TypeConverter<String, Version> versionConverter() {
        return (apiVersionString, targetType, context) -> Optional.ofNullable(Version.getVersionFromString(apiVersionString));
    }

}
