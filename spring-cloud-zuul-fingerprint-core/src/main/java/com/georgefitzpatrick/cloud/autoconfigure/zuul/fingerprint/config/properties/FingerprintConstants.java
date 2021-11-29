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

package com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.config.properties;

/**
 * @author George Fitzpatrick
 */
public final class FingerprintConstants {

    /* ----- Fields ----- */

    public static final String JETTY_SSL_SESSION_ATTRIBUTE = "org.eclipse.jetty.servlet.request.ssl_session";
    public static final String CLIENT_HELLO_ATTRIBUTE = "zuul.fingerprint.ssl.client_hello";
    public static final String HTTP2_SESSION_ATTRIBUTE = "zuul.fingerprint.http2.http2_session";

    /* ----- Constructors ----- */

    private FingerprintConstants() {

    }

}
