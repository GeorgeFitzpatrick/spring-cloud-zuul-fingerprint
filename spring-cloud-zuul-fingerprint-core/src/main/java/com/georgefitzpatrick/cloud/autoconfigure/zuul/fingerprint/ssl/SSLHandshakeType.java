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

import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.ssl.handshake.ClientHello;
import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.util.ByteBufferParser;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author George Fitzpatrick
 */
public enum SSLHandshakeType {

    /* ----- Enumerations ----- */

    UNASSIGNED(SSLRecordType.UNASSIGNED, -1, ByteBuffer::array),

    // assigned
    CCS(SSLRecordType.CHANGE_CIPHER_SPEC, 0x1, ByteBuffer::array),
    WARNINGS(SSLRecordType.ALERT, 0x1, ByteBuffer::array),
    FATAL(SSLRecordType.ALERT, 0x2, ByteBuffer::array),
    HELLO_REQUEST(SSLRecordType.HANDSHAKE, 0x0, ByteBuffer::array),
    CLIENT_HELLO(SSLRecordType.HANDSHAKE, 0x1, ClientHello.PARSER),
    SERVER_HELLO(SSLRecordType.HANDSHAKE, 0x2, ByteBuffer::array),
    NEW_SESSION_TICKET(SSLRecordType.HANDSHAKE, 0x4, ByteBuffer::array),
    CERTIFICATE(SSLRecordType.HANDSHAKE, 0xB, ByteBuffer::array),
    SERVER_KEY_EXCHANGE(SSLRecordType.HANDSHAKE, 0xC, ByteBuffer::array),
    CERTIFICATE_REQUEST(SSLRecordType.HANDSHAKE, 0xD, ByteBuffer::array),
    SERVER_DONE(SSLRecordType.HANDSHAKE, 0xE, ByteBuffer::array),
    CERTIFICATE_VERIFY(SSLRecordType.HANDSHAKE, 0xF, ByteBuffer::array),
    CLIENT_KEY_EXCHANGE(SSLRecordType.HANDSHAKE, 0x10, ByteBuffer::array),
    FINISHED(SSLRecordType.HANDSHAKE, 0x14, ByteBuffer::array),
    CLOSE_NOTIFY(SSLRecordType.APPLICATION_DATA, 0x0, ByteBuffer::array),
    UNEXPECTED_MESSAGE(SSLRecordType.APPLICATION_DATA, 0xA, ByteBuffer::array),
    BAD_RECORD_MAC(SSLRecordType.APPLICATION_DATA, 0x14, ByteBuffer::array),
    DECRYPTION_FAILED(SSLRecordType.APPLICATION_DATA, 0x15, ByteBuffer::array),
    RECORD_OVERFLOW(SSLRecordType.APPLICATION_DATA, 0x16, ByteBuffer::array),
    DECOMPRESSION_FAILURE(SSLRecordType.APPLICATION_DATA, 0x1E, ByteBuffer::array),
    HANDSHAKE_FAILURE(SSLRecordType.APPLICATION_DATA, 0x28, ByteBuffer::array),
    NO_CERTIFICATE(SSLRecordType.APPLICATION_DATA, 0x29, ByteBuffer::array),
    BAD_CERTIFICATE(SSLRecordType.APPLICATION_DATA, 0x2A, ByteBuffer::array),
    UNSUPPORTED_CERTIFICATE(SSLRecordType.APPLICATION_DATA, 0x2B, ByteBuffer::array),
    CERTIFICATE_REVOKED(SSLRecordType.APPLICATION_DATA, 0x2C, ByteBuffer::array),
    CERTIFICATE_EXPIRED(SSLRecordType.APPLICATION_DATA, 0x2D, ByteBuffer::array),
    CERTIFICATE_UNKNOWN(SSLRecordType.APPLICATION_DATA, 0x2E, ByteBuffer::array),
    ILLEGAL_PARAMETER(SSLRecordType.APPLICATION_DATA, 0x2F, ByteBuffer::array),
    UNKNOWN_CA(SSLRecordType.APPLICATION_DATA, 0x30, ByteBuffer::array),
    ACCESS_DENIED(SSLRecordType.APPLICATION_DATA, 0x31, ByteBuffer::array),
    DECODE_ERROR(SSLRecordType.APPLICATION_DATA, 0x32, ByteBuffer::array),
    DECRYPT_ERROR(SSLRecordType.APPLICATION_DATA, 0x33, ByteBuffer::array),
    EXPORT_RESTRICTION(SSLRecordType.APPLICATION_DATA, 0x3C, ByteBuffer::array),
    PROTOCOL_VERSION(SSLRecordType.APPLICATION_DATA, 0x46, ByteBuffer::array),
    INSUFFICIENT_SECURITY(SSLRecordType.APPLICATION_DATA, 0x47, ByteBuffer::array),
    INTERNAL_ERROR(SSLRecordType.APPLICATION_DATA, 0x50, ByteBuffer::array),
    USER_CANCELLED(SSLRecordType.APPLICATION_DATA, 0x5A, ByteBuffer::array),
    NO_RENEGOTIATION(SSLRecordType.APPLICATION_DATA, 0x64, ByteBuffer::array),
    UNSUPPORTED_EXTENSION(SSLRecordType.APPLICATION_DATA, 0x6E, ByteBuffer::array),
    CERTIFICATE_UNOBTAINABLE(SSLRecordType.APPLICATION_DATA, 0x6F, ByteBuffer::array),
    UNRECOGNIZED_NAME(SSLRecordType.APPLICATION_DATA, 0x70, ByteBuffer::array),
    BAD_CERTIFICATE_STATUS_RESPONSE(SSLRecordType.APPLICATION_DATA, 0x71, ByteBuffer::array),
    BAD_CERTIFICATE_HASH_VALUE(SSLRecordType.APPLICATION_DATA, 0x72, ByteBuffer::array),
    UNKNOWN_PSK_IDENTITY(SSLRecordType.APPLICATION_DATA, 0x73, ByteBuffer::array),
    REQUEST(SSLRecordType.HEARTBEAT, 0x1, ByteBuffer::array),
    RESPONSE(SSLRecordType.HEARTBEAT, 0x2, ByteBuffer::array);

    /* ----- Fields ----- */

    private final SSLRecordType recordType;
    private final int value;
    private final ByteBufferParser<?> parser;

    /* ----- Constructors ----- */

    SSLHandshakeType(SSLRecordType recordType, int value, ByteBufferParser<?> parser) {
        this.recordType = recordType;
        this.value = value;
        this.parser = parser;
    }

    /* ----- Methods ----- */

    public static @NotNull SSLHandshakeType valueOf(SSLRecordType recordType, int value) {
        return Arrays.stream(values())
                .filter(handshakeType -> handshakeType.recordType == recordType)
                .filter(handshakeType -> handshakeType.value == value)
                .findFirst()
                .orElse(UNASSIGNED);
    }

    public SSLRecordType recordType() {
        return recordType;
    }

    public int value() {
        return value;
    }

    public ByteBufferParser<?> parser() {
        return parser;
    }

}
