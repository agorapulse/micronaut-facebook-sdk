package com.agorapulse.micronaut.facebooksdk.fsr;

import com.agorapulse.micronaut.facebooksdk.FacebookApplication;
import com.agorapulse.micronaut.facebooksdk.FacebookApplicationConfiguration;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.bind.binders.TypedRequestArgumentBinder;

import javax.inject.Singleton;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.of;

@Singleton
@Requires(classes = TypedRequestArgumentBinder.class)
public class FacebookSignedRequestBinder implements TypedRequestArgumentBinder<FacebookSignedRequest> {

    private static final String SIGNED_REQUEST_PARAMETER_NAME = "signed_request";
    private static final String COOKIE_PREFIX = "fbsr_";

    private final FacebookApplication application;

    public FacebookSignedRequestBinder(FacebookApplication application) {
        this.application = application;
    }

    @Override
    public Argument<FacebookSignedRequest> argumentType() {
        return Argument.of(FacebookSignedRequest.class);
    }

    @Override
    public BindingResult<FacebookSignedRequest> bind(ArgumentConversionContext<FacebookSignedRequest> context, HttpRequest<?> source) {
        FacebookApplicationConfiguration conf = application.getConfiguration();

        Optional<String> requestParameter = source.getParameters().get(SIGNED_REQUEST_PARAMETER_NAME, String.class);
        if (requestParameter.isPresent()) {
            return () -> of(application.parseSignedRequest(requestParameter.get()));
        }

        Optional<?> body = source.getBody();
        if (body.isPresent() && body.get() instanceof Map) {
            Map params = (Map) body.get();
            Object bodyParameter = params.get(SIGNED_REQUEST_PARAMETER_NAME);
            if (bodyParameter != null) {
                return () -> of(application.parseSignedRequest(bodyParameter.toString()));
            }
        }

        return () -> source.getCookies().get(COOKIE_PREFIX + conf.getId(), String.class).map(sr -> FacebookSignedRequest.parse(conf.getSecret(), sr));
    }
}