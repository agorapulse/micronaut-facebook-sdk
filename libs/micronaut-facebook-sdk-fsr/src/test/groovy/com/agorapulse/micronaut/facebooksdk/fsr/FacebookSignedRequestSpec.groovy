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

import groovy.transform.CompileDynamic
import spock.lang.Specification

@CompileDynamic
class FacebookSignedRequestSpec extends Specification {

    public static final FacebookSignedRequest TEST_REQUEST = new FacebookSignedRequest(
            'HMAC-SHA256',
            'c0d€',
            'oauth_token',
            'tokenForBusiness',
            null,
            1562671552,
            10218100942662490
    )

    void 'decode token'() {
        when:
            String secret = 'very-s€cr€t'
            String signedRequestString = TEST_REQUEST.generate(secret)
        then:
            TEST_REQUEST == FacebookSignedRequest.parse(secret, signedRequestString)
    }

}
