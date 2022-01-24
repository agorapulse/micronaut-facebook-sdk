/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2022 Agorapulse.
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
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.core.convert.TypeConverter;

import javax.inject.Singleton;
import java.util.Optional;

@Factory
public class FacebookSdkFactory {

    @EachBean(FacebookApplicationConfiguration.class)
    public FacebookApplication facebookApplication(FacebookApplicationConfiguration configuration) {
        return new DefaultFacebookApplication(configuration);
    }

    @Bean
    @Singleton
    public TypeConverter<String, Version> versionConverter() {
        return (apiVersionString, targetType, context) -> Optional.ofNullable(Version.getVersionFromString(apiVersionString));
    }

}
