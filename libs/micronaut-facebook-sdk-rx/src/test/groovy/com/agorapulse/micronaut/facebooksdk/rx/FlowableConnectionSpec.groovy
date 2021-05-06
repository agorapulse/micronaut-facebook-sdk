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
package com.agorapulse.micronaut.facebooksdk.rx

import com.restfb.Connection
import com.restfb.DefaultFacebookClient
import com.restfb.FacebookClient
import com.restfb.exception.FacebookException
import groovy.transform.CompileDynamic
import io.reactivex.Flowable
import spock.lang.Specification

@CompileDynamic
class FlowableConnectionSpec extends Specification {

    void 'automatic pagination with flowable connection'() {
        given:
            FacebookClient client = Spy(DefaultFacebookClient)
            Connection<String> first = new Connection<>(
                    client,
                    '{ "data" : ["one", "two", "three"], "paging" : { "next" : "https://example.com/foobar" } }',
                    String
            )
            Connection<String> second = new Connection<>(client,  '{ "data" : ["four", "five", "six"] }', String)

            _ * client.fetchConnection('/foo', String) >> first
            _ * client.fetchConnectionPage('https://example.com/foobar', String) >> second
        when:
            Flowable<String> flowable = FlowableConnection.create(client, '/foo', String).flatMap(Flowable.&fromIterable)
        then:
            0 * _

        when:
            List<String> all = flowable.toList().blockingGet()

        then:
            all == ['one', 'two', 'three', 'four', 'five', 'six']
    }

    void 'automatic pagination with flowable connection from groovy'() {
        given:
            FacebookClient client = Spy(DefaultFacebookClient)
            Connection<String> first = new Connection<>(
                    client,
                    '{ "data" : ["one", "two", "three"], "paging" : { "next" : "https://example.com/foobar" } }',
                    String
            )
            Connection<String> second = new Connection<>(client,  '{ "data" : ["four", "five", "six"] }', String)

            _ * client.fetchConnection('/foo', String) >> first
            _ * client.fetchConnectionPage('https://example.com/foobar', String) >> second
        when:
            Flowable<String> flowable = client.fetchFlowable('/foo', String).flatMap(Flowable.&fromIterable)
        then:
            0 * _

        when:
            List<String> all = flowable.toList().blockingGet()

        then:
            all == ['one', 'two', 'three', 'four', 'five', 'six']
    }

    void 'error handling'() {
        given:
            FacebookClient client = Spy(DefaultFacebookClient)

            _ * client.fetchConnection('/foo', String) >> { throw new FacebookException('error') { } }
        when:
            Flowable<String> flowable = FlowableConnection.create(client, '/foo', String).flatMap(Flowable.&fromIterable)
        then:
            0 * _

        when:
            List<String> all = flowable.onErrorReturn { Throwable th -> th.message }.toList().blockingGet()

        then:
            all == ['error']
    }

}
