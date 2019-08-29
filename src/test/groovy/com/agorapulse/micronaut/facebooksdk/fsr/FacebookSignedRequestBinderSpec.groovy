package com.agorapulse.micronaut.facebooksdk.fsr

import com.agorapulse.gru.Gru
import com.agorapulse.gru.http.Http
import com.agorapulse.micronaut.facebooksdk.FacebookApplication
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.context.env.Environment
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Produces
import io.micronaut.http.annotation.Consumes
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.annotation.MicronautTest
import org.junit.Rule
import spock.lang.Specification

import javax.annotation.Nullable
import javax.inject.Inject

@MicronautTest
@CompileDynamic
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
        gru.prepare(server.URL.toExternalForm())
    }

    void 'application exists'() {
        expect:
            application
    }

    void 'anonymous access'() {
        expect:
            gru.test {
                post '/test/fbsr'
                expect {
                    text inline('nothing')
                }
            }
    }

    void 'access with parameter'() {
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

    void 'access with cookie'() {
        given:
            final String COOKIE_NAME = 'fbsr_1234567890'
        expect:
            gru.test {
                post '/test/fbsr', {
                    cookie COOKIE_NAME, FacebookSignedRequestSpec.TEST_REQUEST.generate('secret')
                }
                expect {
                    text inline(FacebookSignedRequestSpec.TEST_REQUEST.userId.toString())
                }
            }
    }

}

@CompileStatic
class SignedRequestBody {

    String signedRequest

    @Override
    String toString() {
        return "SignedRequestBody{signedRequest='$signedRequest'}"
    }

}

@Slf4j
@CompileStatic
@Requires(env = Environment.TEST)
@Controller('/test')
class TestController {

    @Post('/fbsr')
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    String testFacebookSignedRequest(@Nullable FacebookSignedRequest request, @Nullable @Body SignedRequestBody body) {
        log.info('body can be still injected: ' + body)
        if (!request) {
            return 'nothing'
        }
        return request.userId
    }

}
