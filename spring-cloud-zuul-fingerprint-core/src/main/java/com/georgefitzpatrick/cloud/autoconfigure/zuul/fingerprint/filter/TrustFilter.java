/*
 * Copyright 2021 George Fitzpatrick
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.filter;

import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.config.Fingerprinter;
import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.config.KeyGenerator;
import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.config.properties.FingerprintProperties;
import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.config.Bucket;
import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.config.Dataset;
import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.util.Bytes;
import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.util.Matcher;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.config.properties.FingerprintProperties.Policy;
import static com.netflix.zuul.util.HTTPRequestUtils.X_FORWARDED_FOR_HEADER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;
import static org.springframework.http.HttpHeaders.USER_AGENT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * @author George Fitzpatrick
 */
public final class TrustFilter extends ZuulFilter {

    /* ----- Fields ----- */

    private static final double BAD_SCORE = 0;
    private static final double UNCERTAIN_SCORE = 0.5;
    private static final double GOOD_SCORE = 1;

    private static final Logger log = LoggerFactory.getLogger(TrustFilter.class);

    private final FingerprintProperties properties;
    private final List<Fingerprinter> fingerprinters;
    private final Matcher<String> matcher;
    private final RouteLocator routeLocator;
    private final KeyGenerator keyGenerator;
    private final Bucket bucket;
    private final Dataset dataset;

    /* ----- Constructors ----- */

    public TrustFilter(FingerprintProperties properties, List<Fingerprinter> fingerprinters, Matcher<String> matcher,
                       RouteLocator routeLocator, KeyGenerator keyGenerator, Bucket bucket, Dataset dataset) {
        this.properties = properties;
        this.fingerprinters = fingerprinters;
        this.matcher = matcher;
        this.routeLocator = routeLocator;
        this.keyGenerator = keyGenerator;
        this.bucket = bucket;
        this.dataset = dataset;
    }

    /* ----- Methods ----- */

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return properties.getFilterOrder();
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public @Nullable Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest req = ctx.getRequest();

        String path = req.getServletPath();
        Route route = routeLocator.getMatchingRoute(path);

        boolean behindProxy = properties.isBehindProxy();
        String origin = clientIp(req, behindProxy);

        String identity = req.getHeader(USER_AGENT);
        List<String> fingerprints = fingerprint(fingerprinters, ctx);
        List<String> hashes = md5(fingerprints);
        List<String> values = dataset.getAll(hashes);

        int unavailableFingerprints = fingerprinters.size() - fingerprints.size();
        double availableScore = fingerprinters.size() * GOOD_SCORE;

        double achievedScore = unavailableFingerprints * UNCERTAIN_SCORE;
        for (String value : values) {
            if (value == null) {
                achievedScore += UNCERTAIN_SCORE;
            } else if (matcher.matches(identity, value)) {
                achievedScore += GOOD_SCORE;
            } else {
                achievedScore += BAD_SCORE;
            }
        }

        double score = achievedScore / availableScore;
        log.debug("score: {}", score);

        String key = keyGenerator.generate(req, route, origin);
        log.debug("key: {}", key);

        for (Policy policy : properties.getPolicies()) {
            long factor = properties.getScoreWeight(score);
            long remaining = bucket.consume(key, policy, factor);
            if (remaining < 0) {
                ctx.setResponseStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());
                ctx.setSendZuulResponse(false);
            }
        }

        return null;
    }

    private @NotNull List<String> fingerprint(@NotNull List<Fingerprinter> fingerprinters, RequestContext ctx) {
        List<String> fingerprints = new LinkedList<>();

        for (Fingerprinter fingerprinter : fingerprinters) {
            String name = fingerprinter.getClass().getSimpleName();
            String fingerprint = fingerprinter.fingerprint(ctx);

            log.debug("{}: {}", name, fingerprint);

            if (fingerprint != null) fingerprints.add(fingerprint);
        }

        return fingerprints;
    }

    private @NotNull List<String> md5(@NotNull Collection<String> c) throws ZuulException {
        List<String> hashes = new LinkedList<>();

        for (String str : c) {
            String hash = md5(str);
            hashes.add(hash);
        }

        return hashes;
    }

    private String clientIp(@NotNull HttpServletRequest req, boolean behindProxy) {
        String xForwardedFor = req.getHeader(X_FORWARDED_FOR_HEADER);

        if (behindProxy && xForwardedFor != null) {
            return xForwardedFor.split(",")[0].trim();
        }

        return req.getRemoteAddr();
    }

    private @NotNull String md5(String str) throws ZuulException {
        MessageDigest message;
        try {
            message = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new ZuulException(e, INTERNAL_SERVER_ERROR.value(), "");
        }

        message.update(str.getBytes());
        byte[] digest = message.digest();

        return Bytes.toHexString(digest);
    }

}
