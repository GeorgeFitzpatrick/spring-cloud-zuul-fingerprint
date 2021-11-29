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

import org.eclipse.jetty.alpn.server.ALPNServerConnection;
import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.io.AbstractConnection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.ssl.ALPNProcessor.Server;
import org.eclipse.jetty.server.Connector;

import javax.net.ssl.SSLEngine;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author George Fitzpatrick
 */
public final class CustomALPNServerConnectionFactory extends ALPNServerConnectionFactory {

    /* ----- Constructors ----- */

    public CustomALPNServerConnectionFactory(String... protocols) {
        super(protocols);
    }

    /* ----- Methods ----- */

    @Override
    protected AbstractConnection newServerConnection(Connector connector, EndPoint endPoint, SSLEngine engine, List<String> protocols, String defaultProtocol) {
        ALPNServerConnection connection = new ALPNServerConnection(connector, endPoint, engine, protocols, defaultProtocol);

        ServiceLoader<Server> processors = ServiceLoader.load(Server.class);

        Server processor = processors.findFirst().orElseThrow();
        processor.configure(engine, connection);

        return connection;
    }

}
