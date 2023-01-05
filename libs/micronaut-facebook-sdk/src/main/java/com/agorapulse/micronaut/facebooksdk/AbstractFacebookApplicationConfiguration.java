/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2019-2023 Agorapulse.
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
package com.agorapulse.micronaut.facebooksdk;

import com.restfb.Version;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

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

    public void setApiVersion(Version version) {
        setVersion(version.getUrlElement());
    }

    @Override
    public String toString() {
        return "FacebookApplicationConfiguration[id:'" + id + "', permissions:" + permissions + "]";
    }
}
