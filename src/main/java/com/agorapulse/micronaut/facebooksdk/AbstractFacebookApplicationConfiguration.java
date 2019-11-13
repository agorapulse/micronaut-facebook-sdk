package com.agorapulse.micronaut.facebooksdk;

import com.restfb.Version;
import io.micronaut.context.annotation.ConfigurationProperties;

import javax.inject.Named;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Named("default")
@ConfigurationProperties("facebook.sdk.app")
public abstract class AbstractFacebookApplicationConfiguration {

    @NotBlank
    private String secret;

    @NotBlank
    private Long id;

    private String version = Version.LATEST.getUrlElement();

    private List<String> permissions = new ArrayList<>();

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    /**
     * @return the version as String, see {@link #getApiVersion()} to get the {@link Version} enum value
     */
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Version getApiVersion() {
        if (this.version.startsWith("v")) {
            return Version.getVersionFromString(this.version);
        }
        return Version.getVersionFromString("v" + this.version);
    }

    public void setApiVersion(String version) {
        setVersion(version);
    }

    public void setApiVersion(Version version) {
        setVersion(version.getUrlElement());
    }

    @Override
    public String toString() {
        return "FacebookApplicationConfiguration[id:'" + id + "', permissions:" + permissions + "]";
    }
}
