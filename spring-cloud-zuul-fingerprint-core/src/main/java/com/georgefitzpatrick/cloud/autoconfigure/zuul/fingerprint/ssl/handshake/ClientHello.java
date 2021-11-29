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
package com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.ssl.handshake;

import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.ssl.SSLCipherSuite;
import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.ssl.SSLCompressionMethod;
import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.ssl.SSLExtensionType;
import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.ssl.SSLProtocol;
import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.util.ByteBufferParser;
import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.util.Bytes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.*;

import static com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.util.ByteBuffers.*;
import static com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.util.Bytes.UINT16_LENGTH;

/**
 * @author George Fitzpatrick
 */
public final class ClientHello {

    /* ----- Fields ----- */

    public static final int CLIENT_RANDOM_LENGTH = 32;
    public static final ByteBufferParser<ClientHello> PARSER = new Parser();

    private final SSLProtocol clientVersion;
    private final byte[] clientRandom;
    private final byte[] sessionId;
    private final SSLCipherSuite[] cipherSuites;
    private final SSLCompressionMethod[] compressionMethods;
    private final Map<SSLExtensionType, Object> extensions;

    /* ----- Constructors ----- */

    public ClientHello(SSLProtocol clientVersion, byte @NotNull [] clientRandom,
                       byte @NotNull [] sessionId, SSLCipherSuite @NotNull [] cipherSuites,
                       SSLCompressionMethod @NotNull [] compressionMethods, Map<SSLExtensionType, Object> extensions) {
        this.clientVersion = clientVersion;
        this.clientRandom = clientRandom.clone();
        this.sessionId = sessionId.clone();
        this.cipherSuites = cipherSuites.clone();
        this.compressionMethods = compressionMethods.clone();
        this.extensions = extensions;
    }

    /* ----- Methods ----- */

    public @NotNull SSLProtocol clientVersion() {
        return clientVersion;
    }

    public byte @NotNull [] clientRandom() {
        return clientRandom;
    }

    public byte @NotNull [] sessionId() {
        return sessionId;
    }

    public SSLCipherSuite @NotNull [] cipherSuites() {
        return cipherSuites.clone();
    }

    public SSLCompressionMethod @NotNull [] compressionMethods() {
        return compressionMethods.clone();
    }

    public @NotNull Set<SSLExtensionType> extensionTypes() {
        return extensions.keySet();
    }

    public @NotNull Collection<Object> extensions() {
        return extensions.values();
    }

    public @Nullable Object extension(@NotNull SSLExtensionType type) {
        return extensions.get(type);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                .add("clientVersion=" + clientVersion)
                .add("clientRandom=" + Bytes.toString(clientRandom))
                .add("sessionId=" + Bytes.toString(sessionId))
                .add("cipherSuites=" + Arrays.toString(cipherSuites))
                .add("compressionMethods=" + Arrays.toString(compressionMethods))
                .add("extensions=" + extensions)
                .toString();
    }

    /* ----- Classes ----- */

    public static final class Parser implements ByteBufferParser<ClientHello> {

        /* ----- Fields ----- */

        private static final Logger log = LoggerFactory.getLogger(Parser.class);

        /* ----- Constructors ----- */

        private Parser() {

        }

        /* ----- Methods ----- */

        @Override
        public @NotNull ClientHello parse(@NotNull ByteBuffer src) {
            int clientVersionCode = getUint16(src);
            SSLProtocol clientVersion = SSLProtocol.valueOf(clientVersionCode);
            log.debug("{}", clientVersion);

            byte[] clientRandom = getBytes(src, CLIENT_RANDOM_LENGTH);
            log.debug("clientRandom: {}", Bytes.toString(clientRandom));

            int sessionIdLength = src.get();
            byte[] sessionId = getBytes(src, sessionIdLength);
            log.debug("sessionId: {}", Bytes.toString(sessionId));

            int cipherSuitesLength = getUint16(src);
            int[] cipherSuiteCodes = getUint16Array(src, cipherSuitesLength);

            SSLCipherSuite[] cipherSuites = new SSLCipherSuite[cipherSuiteCodes.length];
            for (int i = 0; i < cipherSuiteCodes.length; i++)
                cipherSuites[i] = SSLCipherSuite.valueOf(cipherSuiteCodes[i]);
            log.debug("cipherSuites: {}", Arrays.toString(cipherSuites));

            int compressionMethodsLength = src.get();
            int[] compressionMethodCodes = getUint8Array(src, compressionMethodsLength);

            SSLCompressionMethod[] compressionMethods = new SSLCompressionMethod[compressionMethodCodes.length];
            for (int i = 0; i < compressionMethodCodes.length; i++)
                compressionMethods[i] = SSLCompressionMethod.valueOf(compressionMethodCodes[i]);
            log.debug("compressionMethods: {}", Arrays.toString(compressionMethods));

            // ignore extensions length
            offset(src, UINT16_LENGTH);

            Map<SSLExtensionType, Object> extensions = new HashMap<>();
            while (src.hasRemaining()) {
                int extensionTypeCode = getUint16(src);
                SSLExtensionType extensionType = SSLExtensionType.valueOf(extensionTypeCode);

                int extensionLength = getUint16(src);

                ByteBuffer extensionSrc = getBuffer(src, extensionLength);
                Object extension = extensionType.parser().parse(extensionSrc);

                log.debug("{} ({}): {}", extensionType, extensionTypeCode, extension);

                extensions.put(extensionType, extension);
            }

            return new ClientHello(clientVersion, clientRandom, sessionId, cipherSuites, compressionMethods, extensions);
        }

    }

}