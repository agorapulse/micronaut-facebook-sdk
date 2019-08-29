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
            Flowable<String> flowable = FlowableConnection.create(client, '/foo', String)
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
            Flowable<String> flowable = FlowableConnection.create(client, '/foo', String)
        then:
            0 * _

        when:
            List<String> all = flowable.onErrorReturn { it.message }.toList().blockingGet()

        then:
            all == ['error']
    }

}
