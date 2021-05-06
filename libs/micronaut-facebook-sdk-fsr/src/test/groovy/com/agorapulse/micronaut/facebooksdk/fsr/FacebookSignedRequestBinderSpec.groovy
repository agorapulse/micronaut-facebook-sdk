/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2021 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agorapulse.micronaut.facebooksdk.fsr

import com.agorapulse.gru.Gru
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
import io.micronaut.test.annotation.MicronautTest
import spock.lang.AutoCleanup
import spock.lang.Ignore
import spock.lang.Specification

import javax.annotation.Nullable
import javax.inject.Inject

@MicronautTest
@CompileDynamic
@Property(name = 'facebook.sdk.app.id', value = '1234567890')
@Property(name = 'facebook.sdk.app.secret', value = 'secret')
class FacebookSignedRequestBinderSpec extends Specification {

    @Inject FacebookApplication application

    @AutoCleanup @Inject Gru gru

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

    @Ignore
    void 'access with parameter'() {
        expect:
            gru.test {
                post '/test/fbsr', {
                    headers 'Content-Type': MediaType.APPLICATION_FORM_URLENCODED
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
