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

import java.util.StringJoiner;

/**
 * @author George Fitzpatrick
 */
public final class HTTP2PriorityFrame {

    /* ----- Fields ----- */

    private final int streamId;
    private final boolean exclusive;
    private final int parentStreamId;
    private final int weight;

    /* ----- Constructors ----- */

    public HTTP2PriorityFrame(int streamId, boolean exclusive, int parentStreamId, int weight) {
        this.streamId = streamId;
        this.exclusive = exclusive;
        this.parentStreamId = parentStreamId;
        this.weight = weight;
    }

    /* ----- Methods ----- */

    public int streamId() {
        return streamId;
    }

    public boolean exclusive() {
        return exclusive;
    }

    public int parentStreamId() {
        return parentStreamId;
    }

    public int weight() {
        return weight;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                .add("streamId=" + streamId)
                .add("exclusive=" + exclusive)
                .add("parentStreamId=" + parentStreamId)
                .add("weight=" + weight)
                .toString();
    }

}
