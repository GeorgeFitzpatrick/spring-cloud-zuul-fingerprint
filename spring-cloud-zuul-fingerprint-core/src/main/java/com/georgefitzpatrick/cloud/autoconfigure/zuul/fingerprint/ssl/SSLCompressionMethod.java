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

package com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.ssl;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * @author George Fitzpatrick
 */
public enum SSLCompressionMethod {

    /* ----- Enumerations ----- */

    UNASSIGNED(-1),

    // private
    PRIVATE_0xE0(0xE0),
    PRIVATE_0xE1(0xE1),
    PRIVATE_0xE2(0xE2),
    PRIVATE_0xE3(0xE3),
    PRIVATE_0xE4(0xE4),
    PRIVATE_0xE5(0xE5),
    PRIVATE_0xE6(0xE6),
    PRIVATE_0xE7(0xE7),
    PRIVATE_0xE8(0xE8),
    PRIVATE_0xE9(0xE9),
    PRIVATE_0xEA(0xEA),
    PRIVATE_0xEB(0xEB),
    PRIVATE_0xEC(0xEC),
    PRIVATE_0xED(0xED),
    PRIVATE_0xEE(0xEE),
    PRIVATE_0xEF(0xEF),
    PRIVATE_0xF0(0xF0),
    PRIVATE_0xF1(0xF1),
    PRIVATE_0xF2(0xF2),
    PRIVATE_0xF3(0xF3),
    PRIVATE_0xF4(0xF4),
    PRIVATE_0xF5(0xF5),
    PRIVATE_0xF6(0xF6),
    PRIVATE_0xF7(0xF7),
    PRIVATE_0xF8(0xF8),
    PRIVATE_0xF9(0xF9),
    PRIVATE_0xFA(0xFA),
    PRIVATE_0xFB(0xFB),
    PRIVATE_0xFC(0xFC),
    PRIVATE_0xFD(0xFD),
    PRIVATE_0xFE(0xFE),
    PRIVATE_0xFF(0xFF),

    // assigned
    NULL(0x00),
    DEFLATE(0x01),
    LZS(0x40);

    /* ----- Fields ----- */

    private final int value;

    /* ----- Constructors ----- */

    SSLCompressionMethod(int value) {
        this.value = value;
    }

    /* ----- Methods ----- */

    public static @NotNull SSLCompressionMethod valueOf(int i) {
        return Arrays.stream(values())
                .filter(method -> method.value == i)
                .findFirst()
                .orElse(UNASSIGNED);
    }

    public int value() {
        return value;
    }

}
