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

import javax.net.ssl.*;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.BiFunction;

import static com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.config.properties.FingerprintConstants.CLIENT_HELLO_ATTRIBUTE;

/**
 * @author George Fitzpatrick
 */
public final class SSLEngineWrapper extends SSLEngine {

    /* ----- Fields ----- */

    private final SSLEngine delegate;

    private boolean finished;
    private ClientHello clientHello;

    /* ----- Constructors ----- */

    public SSLEngineWrapper(SSLEngine delegate) {
        this.delegate = delegate;
        this.finished = false;
        this.clientHello = null;
    }

    /* ----- Methods ----- */

    @Override
    public String getPeerHost() {
        return delegate.getPeerHost();
    }

    @Override
    public int getPeerPort() {
        return delegate.getPeerPort();
    }

    @Override
    public SSLEngineResult wrap(ByteBuffer src, ByteBuffer dst) throws SSLException {
        return wrap(new ByteBuffer[]{src}, 0, 1, dst);
    }

    @Override
    public SSLEngineResult wrap(ByteBuffer[] srcs, ByteBuffer dst) throws SSLException {
        if (srcs == null) {
            throw new IllegalArgumentException("src == null");
        } else {
            return wrap(srcs, 0, srcs.length, dst);
        }
    }

    @Override
    public SSLEngineResult wrap(final ByteBuffer[] srcs, final int offset, final int length, final ByteBuffer dst) throws SSLException {
        return delegate.wrap(srcs, offset, length, dst);
    }

    @Override
    public SSLEngineResult unwrap(ByteBuffer src, ByteBuffer dst) throws SSLException {
        return unwrap(src, new ByteBuffer[]{dst}, 0, 1);
    }

    @Override
    public SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts) throws SSLException {
        if (dsts == null) {
            throw new IllegalArgumentException("dsts == null");
        } else {
            return unwrap(src, dsts, 0, dsts.length);
        }
    }

    @Override
    public SSLEngineResult unwrap(final ByteBuffer src, final ByteBuffer[] dsts, final int offset, final int length) throws SSLException {
        if (!finished) {
            if (clientHello != null) {
                SSLSession session = delegate.getHandshakeSession();
                if (session != null) {
                    session.putValue(CLIENT_HELLO_ATTRIBUTE, clientHello);
                    finished = true;
                }
            } else {
                SSLEngineResult.HandshakeStatus status = delegate.getHandshakeStatus();
                if (SSLEngineResult.HandshakeStatus.FINISHED == status) {
                    finished = true;
                } else {
                    SSLRecord record = SSLRecord.parse(src);
                    if (record.handshake() instanceof ClientHello) {
                        clientHello = (ClientHello) record.handshake();
                    }
                }
            }
        }

        return delegate.unwrap(src, dsts, offset, length);
    }

    @Override
    public Runnable getDelegatedTask() {
        return delegate.getDelegatedTask();
    }

    @Override
    public void closeInbound() throws SSLException {
        delegate.closeInbound();
    }

    @Override
    public boolean isInboundDone() {
        return delegate.isInboundDone();
    }

    @Override
    public void closeOutbound() {
        delegate.closeOutbound();
    }

    @Override
    public boolean isOutboundDone() {
        return delegate.isOutboundDone();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return delegate.getSupportedCipherSuites();
    }

    @Override
    public String[] getEnabledCipherSuites() {
        return delegate.getEnabledCipherSuites();
    }

    @Override
    public void setEnabledCipherSuites(final String[] strings) {
        delegate.setEnabledCipherSuites(strings);
    }

    @Override
    public String[] getSupportedProtocols() {
        return delegate.getSupportedProtocols();
    }

    @Override
    public String[] getEnabledProtocols() {
        return delegate.getEnabledProtocols();
    }

    @Override
    public void setEnabledProtocols(final String[] strings) {
        delegate.setEnabledProtocols(strings);
    }

    @Override
    public SSLSession getSession() {
        return delegate.getSession();
    }

    @Override
    public SSLSession getHandshakeSession() {
        return delegate.getHandshakeSession();
    }

    @Override
    public void beginHandshake() throws SSLException {
        delegate.beginHandshake();
    }

    @Override
    public SSLEngineResult.HandshakeStatus getHandshakeStatus() {
        return delegate.getHandshakeStatus();
    }

    @Override
    public boolean getUseClientMode() {
        return delegate.getUseClientMode();
    }


    @Override
    public void setUseClientMode(final boolean b) {
        delegate.setUseClientMode(b);
    }


    @Override
    public boolean getNeedClientAuth() {
        return delegate.getNeedClientAuth();
    }


    @Override
    public void setNeedClientAuth(final boolean b) {
        delegate.setNeedClientAuth(b);
    }


    @Override
    public boolean getWantClientAuth() {
        return delegate.getWantClientAuth();
    }


    @Override
    public void setWantClientAuth(final boolean b) {
        delegate.setWantClientAuth(b);
    }


    @Override
    public boolean getEnableSessionCreation() {
        return delegate.getEnableSessionCreation();
    }

    @Override
    public void setEnableSessionCreation(final boolean b) {
        delegate.setEnableSessionCreation(b);
    }


    @Override
    public SSLParameters getSSLParameters() {
        return delegate.getSSLParameters();
    }

    @Override
    public void setSSLParameters(SSLParameters params) {
        delegate.setSSLParameters(params);
    }

    @Override
    public String getApplicationProtocol() {
        return delegate.getApplicationProtocol();
    }

    @Override
    public String getHandshakeApplicationProtocol() {
        return delegate.getHandshakeApplicationProtocol();
    }

    @Override
    public BiFunction<SSLEngine, List<String>, String> getHandshakeApplicationProtocolSelector() {
        return delegate.getHandshakeApplicationProtocolSelector();
    }

    @Override
    public void setHandshakeApplicationProtocolSelector(BiFunction<SSLEngine, List<String>, String> selector) {
        delegate.setHandshakeApplicationProtocolSelector(selector);
    }

}