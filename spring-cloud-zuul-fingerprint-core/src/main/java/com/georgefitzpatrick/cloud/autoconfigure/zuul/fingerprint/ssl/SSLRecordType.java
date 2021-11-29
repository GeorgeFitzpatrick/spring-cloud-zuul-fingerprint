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
public enum SSLRecordType {

    /* ----- Enumerations ----- */

    UNASSIGNED(-1),

    // assigned
    CHANGE_CIPHER_SPEC(0x14),
    ALERT(0x15),
    HANDSHAKE(0x16),
    APPLICATION_DATA(0x17),
    HEARTBEAT(0x18);

    /* ----- Fields ----- */

    private final int value;

    /* ----- Constructors ----- */

    SSLRecordType(int value) {
        this.value = value;
    }

    /* ----- Methods ----- */

    public static @NotNull SSLRecordType valueOf(int value) {
        return Arrays.stream(values())
                .filter(type -> type.value == value)
                .findFirst()
                .orElse(UNASSIGNED);
    }

    public int value() {
        return value;
    }

}
