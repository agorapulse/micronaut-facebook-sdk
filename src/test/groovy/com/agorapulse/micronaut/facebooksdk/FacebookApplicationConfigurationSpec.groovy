package com.agorapulse.micronaut.facebooksdk

import com.restfb.DefaultFacebookClient
import com.restfb.FacebookClient
import com.restfb.Version
import io.micronaut.context.annotation.Property
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification

import javax.inject.Inject

@MicronautTest
@Property(name = 'facebook.sdk.app.api-version', value = 'v3.2')
class FacebookApplicationConfigurationSpec extends Specification {

    @Inject FacebookApplication application

    void 'application version is converted'() {
        given:
            FacebookClient client = application.createClient()
        expect:
            client instanceof DefaultFacebookClient
            client.apiVersion == Version.VERSION_3_2
    }

}
