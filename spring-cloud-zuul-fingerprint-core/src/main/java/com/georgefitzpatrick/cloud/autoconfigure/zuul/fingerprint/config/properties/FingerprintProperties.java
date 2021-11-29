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

package com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.config.properties;

import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.config.properties.validators.Positive;
import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.config.properties.validators.Score;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author George Fitzpatrick
 */
@Validated
@RefreshScope
@ConfigurationProperties(FingerprintProperties.PREFIX)
public final class FingerprintProperties {

    /* ----- Fields ----- */

    public static final String PREFIX = "zuul.fingerprint";

    @NotNull
    private Boolean enabled = false;

    @Positive
    private Integer filterOrder = 0;

    @NotNull
    private Boolean behindProxy = false;

    @NotNull
    private TreeMap<@Score Double, @Positive Long> scoreWeights = new TreeMap<>();

    @NotNull
    private LinkedList<@Valid @NotNull Policy> policies = new LinkedList<>();

    /* ----- Constructors ----- */

    public FingerprintProperties() {

    }

    /* ----- Methods ----- */

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getFilterOrder() {
        return filterOrder;
    }

    public void setFilterOrder(int filterOrder) {
        this.filterOrder = filterOrder;
    }

    public boolean isBehindProxy() {
        return behindProxy;
    }

    public void setBehindProxy(boolean behindProxy) {
        this.behindProxy = behindProxy;
    }

    public long getScoreWeight(double score) {
        Map.Entry<Double, Long> weight = getScoreWeights().ceilingEntry(score);
        return weight != null ? weight.getValue() : 1L;
    }

    public TreeMap<Double, Long> getScoreWeights() {
        return scoreWeights;
    }

    public void setScoreWeights(TreeMap<Double, Long> scoreWeights) {
        this.scoreWeights = scoreWeights;
    }

    public LinkedList<Policy> getPolicies() {
        return policies;
    }

    public void setPolicies(LinkedList<Policy> policies) {
        this.policies = policies;
    }

    /* ----- Classes ----- */

    public static class Policy {

        /* ----- Fields ----- */

        @Positive
        private Long limit;

        @NotNull
        private Duration refresh;

        @NotNull
        private Map<@Positive Long, @NotNull Duration> penalties = new LinkedHashMap<>();

        /* ----- Constructors ----- */

        public Policy() {

        }

        /* ----- Methods ----- */

        public long getLimit() {
            return limit;
        }

        public void setLimit(long limit) {
            this.limit = limit;
        }

        public Duration getRefresh() {
            return refresh;
        }

        public void setRefresh(Duration refresh) {
            this.refresh = refresh;
        }

        public Duration getPenalty(long exceeded) {
            return getPenalties().get(exceeded);
        }

        public Map<Long, Duration> getPenalties() {
            return penalties;
        }

        public void setPenalties(Map<Long, Duration> penalties) {
            this.penalties = penalties;
        }

    }

}
