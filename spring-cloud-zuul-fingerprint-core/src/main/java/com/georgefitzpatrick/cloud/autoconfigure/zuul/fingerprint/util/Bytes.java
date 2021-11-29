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

import java.util.StringJoiner;

/**
 * @author George Fitzpatrick
 */
public final class Bytes {

    /* ----- Fields ----- */

    public static final int BITMASK = 0xFF;
    public static final int ONE_BYTE = 8;
    public static final int TWO_BYTES = 16;
    public static final int UINT8_LENGTH = 1;
    public static final int UINT16_LENGTH = 2;
    public static final int UINT24_LENGTH = 3;
    private static final char[] HEXES = "0123456789ABCDEF".toCharArray();

    /* ----- Constructors ----- */

    private Bytes() {

    }

    /* ----- Methods ----- */

    public static int toUint24(byte b1, byte b2, byte b3) {
        return ((b1 & BITMASK) << TWO_BYTES) + toUint16(b2, b3);
    }

    public static int toUint16(byte b1, byte b2) {
        return ((b1 & BITMASK) << ONE_BYTE) + toUint8(b2);
    }

    public static int toUint8(byte b) {
        return b & BITMASK;
    }

    public static @NotNull String toHexString(byte @NotNull [] bytes) {
        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {
            String hex = toHexString(b);
            sb.append(hex);
        }

        return sb.toString();
    }

    public static @NotNull String toHexString(byte b) {
        int v = b & BITMASK;
        char[] hex = {HEXES[v >>> 4], HEXES[v & 0x0F]};
        return new String(hex);
    }

    public static @NotNull String toString(byte @NotNull ... bytes) {
        StringJoiner sj = new StringJoiner(":");

        for (byte b : bytes) {
            String hex = toHexString(b);
            sj.add(hex);
        }

        return sj.toString();
    }

}
