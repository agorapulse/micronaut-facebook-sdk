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
            thrown.infoData.isEmpty()
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
