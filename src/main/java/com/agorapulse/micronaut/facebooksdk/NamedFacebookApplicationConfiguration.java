package com.agorapulse.micronaut.facebooksdk;

import io.micronaut.context.annotation.EachProperty;
import io.micronaut.context.annotation.Parameter;

@EachProperty("facebook.sdk.apps")
public class NamedFacebookApplicationConfiguration extends FacebookApplicationConfiguration {

    private final String name;

    public NamedFacebookApplicationConfiguration(@Parameter String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "NamedFacebookApplicationConfiguration[name:'" + name + "', id:'" + getId() + "', permissions:" + getPermissions() + "]";
    }

}
