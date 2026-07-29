package com.agorapulse.micronaut.facebooksdk;

import com.restfb.FacebookClient;
import com.restfb.exception.FacebookException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

/**
 * Wraps a {@link FacebookClient} so that any {@link FacebookException} it throws no longer carries the request
 * metadata restfb attaches to it.
 *
 * <p>restfb populates every {@code FacebookException} with an {@code InfoData} holding the full request URL —
 * including {@code access_token} and {@code appsecret_proof} — which {@code getMessage()}/{@code toString()} then
 * append. Logging the exception (or persisting its message) would otherwise leak those secrets into logs, Sentry and
 * user-facing errors. Clearing the {@code InfoData} neutralises the leak at the source while keeping the Facebook
 * error description (type, code, subcode) intact for diagnostics.</p>
 */
public final class SecretSafeFacebookClient {

    private SecretSafeFacebookClient() {
    }

    public static FacebookClient wrap(FacebookClient delegate) {
        return (FacebookClient) Proxy.newProxyInstance(
                SecretSafeFacebookClient.class.getClassLoader(),
                new Class<?>[]{FacebookClient.class},
                (proxy, method, args) -> {
                    try {
                        return method.invoke(delegate, args);
                    } catch (InvocationTargetException e) {
                        Throwable cause = e.getCause();
                        throw cause != null ? stripSecrets(cause) : e;
                    }
                });
    }

    private static Throwable stripSecrets(Throwable thrown) {
        if (thrown instanceof FacebookException facebookException) {
            facebookException.withInfoData((FacebookException.InfoData) null);
        }
        return thrown;
    }
}
