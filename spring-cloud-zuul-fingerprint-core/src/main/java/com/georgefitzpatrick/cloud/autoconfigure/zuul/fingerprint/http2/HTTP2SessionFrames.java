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

package com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.http2;

import java.util.*;

/**
 * @author George Fitzpatrick
 */
public final class HTTP2SessionFrames {

    /* ----- Fields ----- */

    private final Map<Integer, Integer> settings;
    private final List<Integer> windowUpdates;
    private final List<HTTP2PriorityFrame> priority;

    /* ----- Constructors ----- */

    public HTTP2SessionFrames(Map<Integer, Integer> settings) {
        this.settings = Collections.unmodifiableMap(settings);
        this.windowUpdates = new LinkedList<>();
        this.priority = new LinkedList<>();
    }

    /* ----- Methods ----- */

    public Map<Integer, Integer> settings() {
        return settings;
    }

    public List<Integer> windowUpdate() {
        return windowUpdates;
    }

    public List<HTTP2PriorityFrame> priority() {
        return priority;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                .add("settings=" + settings)
                .add("windowUpdates=" + windowUpdates)
                .add("priority=" + priority)
                .toString();
    }

}
