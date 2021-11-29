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

package com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.ssl.extension;

import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.util.ByteBufferParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.*;

import static com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.util.ByteBuffers.*;
import static com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.util.Bytes.UINT16_LENGTH;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author George Fitzpatrick
 */
public final class ServerName {

    /* ----- Fields ----- */

    public static final Parser PARSER = new Parser();

    private final Map<Integer, String> entries;

    /* ----- Constructors ----- */

    public ServerName(Map<Integer, String> entries) {
        this.entries = entries;
    }

    /* ----- Methods ----- */

    public @NotNull Set<Integer> entryTypes() {
        return entries.keySet();
    }

    public @NotNull Collection<String> entries() {
        return entries.values();
    }

    public @Nullable String entry(int type) {
        return entries.get(type);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                .add("entries=" + entries)
                .toString();
    }

    /* ----- Classes ----- */

    public static final class Parser implements ByteBufferParser<ServerName> {

        /* ----- Constructors ----- */

        private Parser() {

        }

        /* ----- Methods ----- */

        @Override
        public @NotNull ServerName parse(@NotNull ByteBuffer src) {
            Map<Integer, String> entries = new HashMap<>();

            while (src.hasRemaining()) {
                // ignore length
                offset(src, UINT16_LENGTH);

                int type = getUint8(src);

                int entryLength = getUint16(src);
                String entry = getString(src, entryLength, UTF_8);

                entries.put(type, entry);
            }

            return new ServerName(entries);
        }

    }

}
