/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2019-2026 Agorapulse.
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
package com.agorapulse.micronaut.facebooksdk

import com.restfb.FacebookClient
import com.restfb.exception.FacebookException
import com.restfb.exception.FacebookOAuthException
import groovy.transform.CompileDynamic
import spock.lang.Specification

@CompileDynamic
class SecretSafeFacebookClientSpec extends Specification {

    private static final String SECRET_TOKEN = 'leaked-access-token'
    private static final String APP_SECRET_PROOF = 'leaked-appsecret-proof'

    FacebookClient delegate = Mock(FacebookClient)
    FacebookClient client = SecretSafeFacebookClient.wrap(delegate)

    void 'delegates successful calls unchanged'() {
        given:
            delegate.obtainAppSecretProof('token', 'secret') >> 'proof'
        expect:
            client.obtainAppSecretProof('token', 'secret') == 'proof'
    }

    void 'strips request metadata from a thrown facebook exception so secrets are not leaked'() {
        given:
            FacebookException exception = createException('Some Facebook error')
            exception.withInfoData('GET', 'https://graph.facebook.com/123/insights',
                    "access_token=$SECRET_TOKEN&appsecret_proof=$APP_SECRET_PROOF", "Bearer $SECRET_TOKEN", 1L)
            // Sanity check: this is exactly what would leak without the wrapper.
            assert exception.message.contains(SECRET_TOKEN)

            delegate.fetchObject('123', Object) >> { throw exception }
        when:
            client.fetchObject('123', Object)
        then:
            FacebookException thrown = thrown(FacebookException)
            thrown.is(exception)
            thrown.infoData.empty
            !thrown.message.contains(SECRET_TOKEN)
            !thrown.message.contains(APP_SECRET_PROOF)
    }

    void 'leaves non facebook exceptions untouched'() {
        given:
            RuntimeException exception = new IllegalStateException('boom')
            delegate.fetchObject('123', Object) >> { throw exception }
        when:
            client.fetchObject('123', Object)
        then:
            RuntimeException thrown = thrown(RuntimeException)
            thrown.is(exception)
    }

    private static FacebookException createException(String message) {
        return new FacebookOAuthException(null, message, null, null, null, null, null, null, null)
    }

}
