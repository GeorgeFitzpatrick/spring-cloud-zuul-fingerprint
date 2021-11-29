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

import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.config.Dataset;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author George Fitzpatrick
 */
public final class RedisDataset implements Dataset {

    /* ----- Fields ----- */

    private final ValueOperations<String, String> operations;

    /* ----- Constructors ----- */

    public RedisDataset(@NotNull StringRedisTemplate redis) {
        this.operations = redis.opsForValue();
    }

    /* ----- Methods ----- */

    @Override
    public @Nullable String get(String key) {
        return operations.get(key);
    }

    @Override
    public @NotNull List<String> getAll(Collection<String> keys) {
        List<String> identities = operations.multiGet(keys);
        return identities != null ? identities : Collections.emptyList();
    }

    @Override
    public void set(@NotNull String key, String value) {
        operations.set(key, value);
    }

    @Override
    public void setAll(@NotNull Map<String, String> data) {
        operations.multiSet(data);
    }

}