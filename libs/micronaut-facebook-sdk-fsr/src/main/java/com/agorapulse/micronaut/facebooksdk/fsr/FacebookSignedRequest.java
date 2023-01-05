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
package com.agorapulse.micronaut.facebooksdk.fsr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.beans.ConstructorProperties;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class FacebookSignedRequest {

    private static final String HMAC_SHA_256 = "HmacSHA256";
    private static final ObjectMapper JSON = new ObjectMapper();

    static {
        JSON.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

    public static FacebookSignedRequest parse(String appSecret, String signature)  {
        String[] signedRequestParts = signature.trim().split("\\.");

        if (signedRequestParts.length != 2) {
            throw new IllegalArgumentException("Invalid Signed Request: " + signature);
        }

        String encodedSignature = decodeUrlSafe(signedRequestParts[0]);
        String encodedParameters = decodeUrlSafe(signedRequestParts[1]);

        // Validate signature
        Mac hmacSha256 = buildMac(appSecret);

        byte[] expectedSignature = hmacSha256.doFinal(encodedParameters.getBytes());

        Base64.Decoder decoder = Base64.getDecoder();

        if (!Arrays.equals(expectedSignature, decoder.decode(encodedSignature))) {
            throw new IllegalArgumentException("Invalid signed request");
        }

        // Decode parameters
        try {
            return JSON.readValue(decoder.decode(encodedParameters), FacebookSignedRequest.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot decode signature", e);
        }
    }

    private String algorithm;
    private String code;

    private String oauthToken;
    private String tokenForBusiness;

    private Long expires;
    private Long issuedAt;
    private Long userId;

    @ConstructorProperties({"algorithm", "code", "oauthToken", "tokenForBusiness", "expires", "issuedAt", "userId"})
    public FacebookSignedRequest(String algorithm, String code, String oauthToken, String tokenForBusiness, Long expires, Long issuedAt, Long userId) {
        this.algorithm = algorithm;
        this.code = code;
        this.oauthToken = oauthToken;
        this.tokenForBusiness = tokenForBusiness;
        this.expires = expires;
        this.issuedAt = issuedAt;
        this.userId = userId;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getCode() {
        return code;
    }

    public String getOauthToken() {
        return oauthToken;
    }

    public String getTokenForBusiness() {
        return tokenForBusiness;
    }

    public Long getExpires() {
        return expires;
    }

    public Long getIssuedAt() {
        return issuedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public String generate(String appSecret) {
        try {
            Mac hmacSha256 = buildMac(appSecret);
            Base64.Encoder encoder = Base64.getEncoder();

            String encodedParameters = encoder.encodeToString(JSON.writeValueAsBytes(this));
            String encodedSignature = encoder.encodeToString(hmacSha256.doFinal(encodedParameters.getBytes()));

            return encodeAsUrlSafe(encodedSignature) + "." + encodeAsUrlSafe(encodedParameters);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot render self as JSON", e);
        }
    }

    //CHECKSTYLE:OFF
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FacebookSignedRequest that = (FacebookSignedRequest) o;
        return Objects.equals(algorithm, that.algorithm) &&
                Objects.equals(code, that.code) &&
                Objects.equals(oauthToken, that.oauthToken) &&
                Objects.equals(tokenForBusiness, that.tokenForBusiness) &&
                Objects.equals(expires, that.expires) &&
                Objects.equals(issuedAt, that.issuedAt) &&
                Objects.equals(userId, that.userId);
    }
    //CHECKSTYLE:ON

    @Override
    public int hashCode() {
        return Objects.hash(algorithm, code, oauthToken, tokenForBusiness, expires, issuedAt, userId);
    }

    private static Mac buildMac(String appSecret) {
        try {
            Mac hmacSha256 = Mac.getInstance(HMAC_SHA_256);
            hmacSha256.init(new SecretKeySpec(appSecret.getBytes(), HMAC_SHA_256));
            return hmacSha256;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalArgumentException("Cannot build Mac for application secret", e);
        }
    }

    private static String encodeAsUrlSafe(String s) {
        return s.replace('/', '_').replace('+', '-');
    }

    private static String decodeUrlSafe(String s) {
        return s.replace('_', '/').replace('-', '+');
    }

}
