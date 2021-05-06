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
package com.agorapulse.micronaut.facebooksdk

import com.restfb.DefaultFacebookClient
import com.restfb.FacebookClient
import com.restfb.Version
import groovy.transform.CompileDynamic
import io.micronaut.context.annotation.Property
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification

import javax.inject.Inject

@MicronautTest
@CompileDynamic
@Property(name = 'facebook.sdk.app.api-version', value = 'v3.2')
class FacebookApplicationConfigurationSpec extends Specification {

    @Inject
    FacebookApplication application

    void 'application version is converted'() {
        given:
            FacebookClient client = application.createClient()
        expect:
            client instanceof DefaultFacebookClient
            client.apiVersion == Version.VERSION_3_2
    }

}
