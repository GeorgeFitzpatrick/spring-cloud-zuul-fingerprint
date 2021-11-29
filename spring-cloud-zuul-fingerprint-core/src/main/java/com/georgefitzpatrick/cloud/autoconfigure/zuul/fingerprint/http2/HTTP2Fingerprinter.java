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

import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.config.Fingerprinter;
import com.netflix.zuul.context.RequestContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.StringJoiner;

import static com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.config.properties.FingerprintConstants.HTTP2_SESSION_ATTRIBUTE;
import static java.lang.String.format;
import static org.eclipse.jetty.http.HttpVersion.HTTP_2;

public final class HTTP2Fingerprinter implements Fingerprinter {

    /* ----- Constructors ----- */

    public HTTP2Fingerprinter() {

    }

    /* ----- Methods ----- */

    @Override
    public @Nullable String fingerprint(@NotNull RequestContext ctx) {
        HttpServletRequest req = ctx.getRequest();
        HttpSession session = req.getSession();

        String protocol = ctx.getRequest().getProtocol();
        if (!HTTP_2.toString().equals(protocol)) return null;

        HTTP2SessionFrames frames = (HTTP2SessionFrames) ctx.get(HTTP2_SESSION_ATTRIBUTE);
        if (frames != null) {
            session.setAttribute(HTTP2_SESSION_ATTRIBUTE, frames);
        } else {
            frames = (HTTP2SessionFrames) session.getAttribute(HTTP2_SESSION_ATTRIBUTE);
        }

        if (frames == null) return null;

        StringJoiner settings = new StringJoiner(";");
        frames.settings().forEach((key, value) ->
                settings.add(format("%s:%s", key, value)));

        StringJoiner windowUpdates = new StringJoiner(",");
        frames.windowUpdate().forEach(increment ->
                windowUpdates.add(increment.toString()));

        StringJoiner streamPriorities = new StringJoiner(",");
        frames.priority().forEach(priority ->
                streamPriorities.add(format("%s:%s:%s:%s", priority.streamId(),
                        priority.exclusive() ? 1 : 0, priority.parentStreamId(), priority.weight())));

        StringJoiner fingerprint = new StringJoiner("|");
        fingerprint.add(settings.toString());
        fingerprint.add(windowUpdates.toString());
        fingerprint.add(streamPriorities.toString());

        return fingerprint.toString();
    }

}
