package com.agorapulse.micronaut.facebooksdk.fsr

import com.agorapulse.gru.Gru
import com.agorapulse.gru.http.Http
import com.agorapulse.micronaut.facebooksdk.FacebookApplication
import groovy.util.logging.Slf4j
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.context.env.Environment
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.annotation.MicronautTest
import org.junit.Rule
import spock.lang.Specification

import javax.annotation.Nullable
import javax.inject.Inject

@MicronautTest
@Property(name = 'facebook.sdk.app.id', value = '1234567890')
@Property(name = 'facebook.sdk.app.secret', value = 'secret')
class FacebookSignedRequestBinderSpec extends Specification {

    @Inject
    EmbeddedServer server

    @Inject
    FacebookApplication application

    @Rule
    Gru gru = Gru.equip(Http.steal(this))

    void setup() {
        gru.prepare(server.getURL().toExternalForm())
    }

    void 'application exists'() {
        expect:
            application
    }

    void 'test anonymous'() {
        expect:
            gru.test {
                post '/test/fbsr'
                expect {
                    text inline('nothing')
                }
            }
    }

    void 'test parameter'() {
        expect:
            gru.test {
                post '/test/fbsr', {
                    params signed_request: FacebookSignedRequestSpec.TEST_REQUEST.generate('secret')
                }
                expect {
                    text inline(FacebookSignedRequestSpec.TEST_REQUEST.userId.toString())
                }
            }
    }

    void 'test cookie'() {
        given:
            final String cookieName = 'fbsr_' + 1234567890;
        expect:
            gru.test {
                post '/test/fbsr', {
                    cookie cookieName, FacebookSignedRequestSpec.TEST_REQUEST.generate('secret')
                }
                expect {
                    text inline(FacebookSignedRequestSpec.TEST_REQUEST.userId.toString())
                }
            }
    }

}

class SignedRequestBody {
    String signed_request

    @Override
    String toString() {
        return "SignedRequestBody{" +
                "signed_request='" + signed_request + '\'' +
                '}';
    }
}

@Slf4j
@Requires(env = Environment.TEST)
@Controller('/test')
class TestController {

    @Post('/fbsr')
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    String testFacebookSignedRequest(@Nullable FacebookSignedRequest request, @Nullable @Body SignedRequestBody body) {
        log.info('body can be still injected: ' + body)
        if (!request) {
            return "nothing"
        }
        return request.userId
    }

}
