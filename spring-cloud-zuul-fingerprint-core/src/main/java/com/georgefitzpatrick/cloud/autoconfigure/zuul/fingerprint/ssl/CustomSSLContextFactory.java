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

import org.eclipse.jetty.util.ssl.SslContextFactory;

import javax.net.ssl.SSLEngine;
import java.net.InetSocketAddress;

/**
 * @author George Fitzpatrick
 */
public class CustomSSLContextFactory extends SslContextFactory.Server {

    /* ----- Constructors ----- */

    public CustomSSLContextFactory() {

    }

    /* ----- Methods ----- */

    @Override
    public SSLEngine newSSLEngine() {
        SSLEngine delegate = super.newSSLEngine();
        return new SSLEngineWrapper(delegate);
    }

    @Override
    public SSLEngine newSSLEngine(String host, int port) {
        SSLEngine delegate = super.newSSLEngine();
        return new SSLEngineWrapper(delegate);
    }

    @Override
    public SSLEngine newSSLEngine(InetSocketAddress address) {
        SSLEngine delegate = super.newSSLEngine();
        return new SSLEngineWrapper(delegate);
    }

}
