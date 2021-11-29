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

package com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.config.data.redis;

import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.config.data.AbstractBucket;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

/**
 * @author George Fitzpatrick
 */
public final class RedisBucket extends AbstractBucket {

    /* ----- Fields ----- */

    private final StringRedisTemplate redis;
    private final ValueOperations<String, String> operations;

    /* ----- Constructors ----- */

    public RedisBucket(@NotNull StringRedisTemplate redis) {
        this.redis = redis;
        this.operations = redis.opsForValue();
    }

    /* ----- Methods ----- */

    @Override
    public long get(String key) {
        String usage = operations.get(key);
        return usage != null ? Long.parseLong(usage) : 0;
    }

    @Override
    protected long increment(String key, long value) {
        Long usage = operations.increment(key, value);
        return usage != null ? usage : 0;
    }

    @Override
    protected void expire(String key, Duration duration) {
        redis.expire(key, duration);
    }

}
