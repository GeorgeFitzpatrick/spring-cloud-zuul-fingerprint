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
public enum SSLProtocol {

    /* ----- Enumerations ----- */

    UNASSIGNED(-1),

    // ssl
    SSL_v1_0(0x0100),
    SSL_v2_0(0x0200),
    SSL_V3_0(0x0300),

    // tls
    TLS_v1_0(0x0301),
    TLS_v1_1(0x0302),
    TLS_v1_2(0x0303),
    TLS_v1_3(0x0304),

    // dtls
    DTLS_1_0(0xfeff),
    DTLS_1_1(0xfefd);

    /* ----- Fields ----- */

    private final int value;

    /* ----- Constructors ----- */

    SSLProtocol(int value) {
        this.value = value;
    }

    /* ----- Methods ----- */

    public static @NotNull SSLProtocol valueOf(int i) {
        return Arrays.stream(values())
                .filter(protocol -> protocol.value == i)
                .findFirst()
                .orElse(UNASSIGNED);
    }

    public int value() {
        return value;
    }

}
