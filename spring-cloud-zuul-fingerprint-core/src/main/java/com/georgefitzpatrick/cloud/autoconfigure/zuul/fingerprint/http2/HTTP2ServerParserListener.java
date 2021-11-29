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

import com.netflix.zuul.context.RequestContext;
import org.eclipse.jetty.http2.frames.*;
import org.eclipse.jetty.http2.parser.ServerParser;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.config.properties.FingerprintConstants.HTTP2_SESSION_ATTRIBUTE;

/**
 * @author George Fitzpatrick
 */
public final class HTTP2ServerParserListener implements ServerParser.Listener {

    /* ----- Fields ----- */

    private final ServerParser.Listener delegate;

    /* ----- Constructors ----- */

    public HTTP2ServerParserListener(ServerParser.Listener delegate) {
        this.delegate = delegate;
    }

    /* ----- Methods ----- */

    @Override
    public void onPreface() {
        delegate.onPreface();
    }

    @Override
    public void onData(DataFrame frame) {
        delegate.onData(frame);
    }

    @Override
    public void onHeaders(HeadersFrame frame) {
        delegate.onHeaders(frame);
    }

    @Override
    public void onPriority(@NotNull PriorityFrame frame) {
        int streamId = frame.getStreamId();
        boolean exclusive = frame.isExclusive();
        int dependentStreamId = frame.getParentStreamId();
        int weight = frame.getWeight();
        HTTP2PriorityFrame priority = new HTTP2PriorityFrame(streamId, exclusive, dependentStreamId, weight);
        RequestContext ctx = RequestContext.getCurrentContext();
        HTTP2SessionFrames session = (HTTP2SessionFrames) ctx.get(HTTP2_SESSION_ATTRIBUTE);
        session.priority().add(priority);
        delegate.onPriority(frame);
    }

    @Override
    public void onReset(ResetFrame frame) {
        delegate.onReset(frame);
    }

    @Override
    public void onSettings(@NotNull SettingsFrame frame) {
        Map<Integer, Integer> settings = frame.getSettings();
        HTTP2SessionFrames session = new HTTP2SessionFrames(settings);
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.set(HTTP2_SESSION_ATTRIBUTE, session);
        delegate.onSettings(frame);
    }

    @Override
    public void onPushPromise(PushPromiseFrame frame) {
        delegate.onPushPromise(frame);
    }

    @Override
    public void onPing(PingFrame frame) {
        delegate.onPing(frame);
    }

    @Override
    public void onGoAway(GoAwayFrame frame) {
        delegate.onGoAway(frame);
    }

    @Override
    public void onWindowUpdate(@NotNull WindowUpdateFrame frame) {
        int increment = frame.getWindowDelta();
        RequestContext ctx = RequestContext.getCurrentContext();
        HTTP2SessionFrames session = (HTTP2SessionFrames) ctx.get(HTTP2_SESSION_ATTRIBUTE);
        session.windowUpdate().add(increment);
        delegate.onWindowUpdate(frame);
    }

    @Override
    public void onStreamFailure(int i, int i1, String s) {
        delegate.onStreamFailure(i, i1, s);
    }

    @Override
    public void onConnectionFailure(int i, String s) {
        delegate.onConnectionFailure(i, s);
    }

}
