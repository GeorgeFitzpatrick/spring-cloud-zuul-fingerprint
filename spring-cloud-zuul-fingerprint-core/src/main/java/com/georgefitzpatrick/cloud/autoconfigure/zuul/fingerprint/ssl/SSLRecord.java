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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.StringJoiner;

import static com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.util.ByteBuffers.*;
import static com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.util.Bytes.UINT16_LENGTH;
import static com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.util.Bytes.UINT24_LENGTH;

/**
 * @author George Fitzpatrick
 */
public final class SSLRecord {

    /* ----- Fields ----- */

    private static final int MIN_PACKET_LENGTH = 4;

    private static final Logger log = LoggerFactory.getLogger(SSLRecord.class);

    private final SSLRecordType type;
    private final SSLProtocol protocol;
    private final SSLHandshakeType handshakeType;
    private final Object handshake;

    /* ----- Constructors ----- */

    public SSLRecord(SSLRecordType type, SSLProtocol protocol, SSLHandshakeType handshakeType, Object handshake) {
        this.type = type;
        this.protocol = protocol;
        this.handshakeType = handshakeType;
        this.handshake = handshake;
    }

    /* ----- Methods ----- */

    public static @NotNull SSLRecord parse(@NotNull ByteBuffer src) {
        if (src.remaining() < MIN_PACKET_LENGTH) {
            String msg = String.format("%s below minimum SSL record length (%s)", src.remaining(), MIN_PACKET_LENGTH);
            throw new IllegalStateException(msg);
        }

        ByteBuffer dup = src.duplicate();

        int typeCode = getUint8(dup);
        SSLRecordType type = SSLRecordType.valueOf(typeCode);
        log.debug("{}", type);

        int protocolCode = getUint16(dup);
        SSLProtocol protocol = SSLProtocol.valueOf(protocolCode);
        log.debug("{}", protocol);

        // ignore length
        offset(dup, UINT16_LENGTH);

        int handshakeTypeCode = getUint8(dup);
        SSLHandshakeType handshakeType = SSLHandshakeType.valueOf(type, handshakeTypeCode);
        log.debug("{}", handshakeType);

        // ignore handshake length
        offset(dup, UINT24_LENGTH);

        Object handshake = handshakeType.parser().parse(dup);

        return new SSLRecord(type, protocol, handshakeType, handshake);
    }

    public @NotNull SSLRecordType type() {
        return type;
    }

    public @NotNull SSLProtocol protocol() {
        return protocol;
    }

    public @NotNull SSLHandshakeType handshakeType() {
        return handshakeType;
    }

    public @NotNull Object handshake() {
        return handshake;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                .add("type=" + type)
                .add("protocol=" + protocol)
                .add("handshakeType=" + handshakeType)
                .add("handshake=" + handshake)
                .toString();
    }

}
