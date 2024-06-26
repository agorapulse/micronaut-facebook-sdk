/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2019-2024 Agorapulse.
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
import io.micronaut.context.ApplicationContext
import spock.lang.AutoCleanup
import spock.lang.Specification

@CompileDynamic
class FacebookApplicationConfigurationSpec extends Specification {

    @AutoCleanup ApplicationContext context

    void 'application version is converted'() {
        given:
            context = ApplicationContext.builder(
                    'facebook.sdk.app.api-version': 'v16.0',
                    'facebook.sdk.app.id': '1234567890',
                    'facebook.sdk.app.secret': 'secret'
            ).build()
            context.start()

            FacebookApplication application = context.getBean(FacebookApplication)
            FacebookClient client = application.createClient()
        expect:
            client instanceof DefaultFacebookClient
            client.apiVersion == Version.VERSION_16_0
    }

    void 'app id is optional'() {
        given:
            context = ApplicationContext.builder(
                    'facebook.sdk.app.api-version': 'v16.0',
                    'facebook.sdk.app.secret': 'secret'
            ).build()
            context.start()

            FacebookApplication application = context.getBean(FacebookApplication)
            FacebookClient client = application.createClient()
        expect:
            client instanceof DefaultFacebookClient
            client.apiVersion == Version.VERSION_16_0
    }

}
