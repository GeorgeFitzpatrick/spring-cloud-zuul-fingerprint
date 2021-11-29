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

package com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.util;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import static com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.util.Bytes.*;

/**
 * @author George Fitzpatrick
 */
public final class ByteBuffers {

    /* ----- Constructors ----- */

    private ByteBuffers() {

    }

    /* ----- Methods ----- */

    public static @NotNull ByteBuffer getBuffer(@NotNull ByteBuffer src, int length) {
        int limit = src.position() + length;
        ByteBuffer copy = src.duplicate();
        copy.limit(limit);

        offset(src, length);

        return copy;
    }

    public static void offset(@NotNull ByteBuffer src, int increment) {
        int pos = src.position();
        src.position(pos + increment);
    }

    public static int @NotNull [] getAsUint8Array(ByteBuffer src) {
        return ByteBuffers.getUint8Array(src, src.remaining());
    }

    public static int @NotNull [] getUint8Array(ByteBuffer src, int length) {
        int[] values = new int[length];
        for (int i = 0; i < length; i++) {
            values[i] = getUint8(src);
        }

        return values;
    }

    public static int getUint8(ByteBuffer src) {
        byte[] bytes = getBytes(src, UINT8_LENGTH);
        return Bytes.toUint8(bytes[0]);
    }

    public static byte @NotNull [] getBytes(@NotNull ByteBuffer src, int length) {
        byte[] bytes = new byte[length];
        src.get(bytes, 0, length);
        return bytes;
    }

    public static int @NotNull [] getAsUint16Array(ByteBuffer src) {
        return ByteBuffers.getUint16Array(src, src.remaining());
    }

    public static int @NotNull [] getUint16Array(ByteBuffer src, int length) {
        int uint16ArrayLength = length / UINT16_LENGTH;
        int[] values = new int[uint16ArrayLength];

        for (int i = 0; i < uint16ArrayLength; i++) {
            values[i] = getUint16(src);
        }

        return values;
    }

    public static int getUint16(ByteBuffer src) {
        byte[] bytes = getBytes(src, UINT16_LENGTH);
        return Bytes.toUint16(bytes[0], bytes[1]);
    }

    public static int getUInt24(ByteBuffer src) {
        byte[] bytes = getBytes(src, UINT24_LENGTH);
        return Bytes.toUint24(bytes[0], bytes[1], bytes[2]);
    }

    public static @NotNull String getString(ByteBuffer src, int length, Charset charset) {
        byte[] bytes = getBytes(src, length);
        return new String(bytes, charset);
    }

}
