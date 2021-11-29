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

package com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.http2;

import org.eclipse.jetty.http2.parser.RateControl;
import org.eclipse.jetty.http2.parser.ServerParser;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;

/**
 * @author George Fitzpatrick
 */
public final class HTTP2ConnectionFactory extends HTTP2ServerConnectionFactory {

    /* ----- Constructors ----- */

    public HTTP2ConnectionFactory(HttpConfiguration httpConfiguration) {
        super(httpConfiguration);
    }

    /* ----- Methods ----- */

    @Override
    protected ServerParser newServerParser(Connector connector, ServerParser.Listener delegate, RateControl rateControl) {
        HTTP2ServerParserListener listener = new HTTP2ServerParserListener(delegate);
        return super.newServerParser(connector, listener, rateControl);
    }

}
