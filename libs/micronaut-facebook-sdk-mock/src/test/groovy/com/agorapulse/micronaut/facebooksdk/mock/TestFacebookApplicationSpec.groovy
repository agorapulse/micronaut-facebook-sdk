package com.agorapulse.micronaut.facebooksdk.mock

import com.agorapulse.testing.fixt.Fixt
import com.restfb.types.User
import groovy.transform.CompileDynamic
import io.micronaut.test.annotation.MicronautTest
import spock.lang.AutoCleanup
import spock.lang.Specification

import javax.inject.Inject

@MicronautTest
@CompileDynamic
class TestFacebookApplicationSpec extends Specification {

    Fixt fixt = Fixt.create(TestFacebookApplicationSpec)

    @Inject @AutoCleanup TestFacebookApplication facebook

    void 'get me'() {
        given:
            facebook.mockApi {
                get('/v10.0/me') {
                    just fixt.readText('me.json')
                }
            }

        when:
            User me = facebook.createClient().fetchObject('/me', User)

        then:
            me
            me.name == 'Facebook SDK'
    }

}
