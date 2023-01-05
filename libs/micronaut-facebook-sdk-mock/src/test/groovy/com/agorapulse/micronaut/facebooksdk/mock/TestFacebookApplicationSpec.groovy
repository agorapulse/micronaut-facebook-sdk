/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2019-2023 Agorapulse.
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
                get('/v12.0/me') {
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
