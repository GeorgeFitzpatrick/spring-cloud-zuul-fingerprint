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

import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.config.Fingerprinter;
import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.ssl.extension.ECPointFormats;
import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.ssl.extension.SupportedGroups;
import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.ssl.handshake.ClientHello;
import com.netflix.zuul.context.RequestContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLSession;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.stream.IntStream;

import static com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.config.properties.FingerprintConstants.CLIENT_HELLO_ATTRIBUTE;
import static com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.config.properties.FingerprintConstants.JETTY_SSL_SESSION_ATTRIBUTE;
import static com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.ssl.SSLExtensionType.EC_POINT_FORMATS;
import static com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.ssl.SSLExtensionType.SUPPORTED_GROUPS;

public final class JA3Fingerprinter implements Fingerprinter {

    /* ----- Fields ----- */

    private static final int[] GREASE_TABLE = {
            0x0a0a, 0x1a1a, 0x2a2a, 0x3a3a, 0x4a4a, 0x5a5a, 0x6a6a, 0x7a7a,
            0x8a8a, 0x9a9a, 0xaaaa, 0xbaba, 0xcaca, 0xdada, 0xeaea, 0xfafa
    };

    /* ----- Constructors ----- */

    public JA3Fingerprinter() {

    }

    /* ----- Methods ----- */

    @Override
    public @Nullable String fingerprint(@NotNull RequestContext ctx) {
        HttpServletRequest req = ctx.getRequest();

        SSLSession session = (SSLSession) req.getAttribute(JETTY_SSL_SESSION_ATTRIBUTE);
        if (session == null) return null;

        ClientHello clientHello = (ClientHello) session.getValue(CLIENT_HELLO_ATTRIBUTE);
        if (clientHello == null) return null;

        StringJoiner fingerprint = new StringJoiner(",");

        int protocol = clientHello.clientVersion().value();
        String strProtocol = String.valueOf(protocol);
        fingerprint.add(strProtocol);

        int[] cipherSuites = cipherSuites(clientHello);
        write(fingerprint, cipherSuites);

        int[] extensionTypes = extensionsTypes(clientHello);
        write(fingerprint, extensionTypes);

        int[] supportedGroups = supportedGroups(clientHello);
        write(fingerprint, supportedGroups);

        int[] ecPointFormats = ecPointFormats(clientHello);
        write(fingerprint, ecPointFormats);

        return fingerprint.toString();
    }

    private int @NotNull [] cipherSuites(@NotNull ClientHello clientHello) {
        return Arrays.stream(clientHello.cipherSuites()).mapToInt(SSLCipherSuite::value).toArray();
    }

    private void write(StringJoiner fingerprint, int @NotNull [] iArr) {
        StringJoiner sj = new StringJoiner("-");

        for (int i : iArr) {
            if (isNotGrease(i)) {
                sj.add(String.valueOf(i));
            }
        }

        String value = sj.toString();
        fingerprint.add(value);
    }

    private int @NotNull [] extensionsTypes(@NotNull ClientHello clientHello) {
        return clientHello.extensionTypes().stream().mapToInt(SSLExtensionType::value).toArray();
    }

    private int @NotNull [] supportedGroups(@NotNull ClientHello clientHello) {
        Object extension = clientHello.extension(SUPPORTED_GROUPS);

        if (extension == null) return new int[0];

        return ((SupportedGroups) extension).groups().stream().mapToInt(i -> i).toArray();
    }

    private int @NotNull [] ecPointFormats(@NotNull ClientHello clientHello) {
        Object extension = clientHello.extension(EC_POINT_FORMATS);

        if (extension == null) return new int[0];

        return ((ECPointFormats) extension).formats().stream().mapToInt(i -> i).toArray();
    }

    private boolean isNotGrease(int i) {
        return IntStream.of(GREASE_TABLE).noneMatch(x -> x == i);
    }

}
