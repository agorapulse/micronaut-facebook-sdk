package com.agorapulse.micronaut.facebooksdk.fsr

import com.agorapulse.micronaut.facebooksdk.fsr.FacebookSignedRequest
import spock.lang.Specification

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
