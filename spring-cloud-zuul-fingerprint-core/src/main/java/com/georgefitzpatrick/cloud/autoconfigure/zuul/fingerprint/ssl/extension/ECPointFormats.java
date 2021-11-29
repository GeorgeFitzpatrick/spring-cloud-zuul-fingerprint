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

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

import static com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.util.ByteBuffers.getUint8;
import static com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.util.ByteBuffers.offset;
import static com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.util.Bytes.UINT8_LENGTH;

/**
 * @author George Fitzpatrick
 */
public final class ECPointFormats {

    /* ----- Fields ----- */

    public static final ByteBufferParser<ECPointFormats> PARSER = new Parser();

    private final List<Integer> formats;

    /* ----- Constructors ----- */

    public ECPointFormats(List<Integer> formats) {
        this.formats = formats;
    }

    /* ----- Methods ----- */

    public List<Integer> formats() {
        return formats;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                .add("formats=" + formats)
                .toString();
    }

    /* ----- Classes ----- */

    public static final class Parser implements ByteBufferParser<ECPointFormats> {

        /* ----- Constructors ----- */

        private Parser() {

        }

        /* ----- Methods ----- */

        @Override
        public @NotNull ECPointFormats parse(@NotNull ByteBuffer src) {
            List<Integer> formats = new LinkedList<>();

            // ignore length
            offset(src, UINT8_LENGTH);

            while (src.hasRemaining()) {
                int format = getUint8(src);
                formats.add(format);
            }

            return new ECPointFormats(formats);
        }

    }

}
