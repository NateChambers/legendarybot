/*
 * MIT License
 *
 * Copyright (c) Copyright (c) 2017-2017, Greatmancode
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.greatmancode.legendarybot;

import com.greatmancode.legendarybot.api.LegendaryBot;
import com.greatmancode.legendarybot.api.utils.StacktraceHandler;
import io.sentry.Sentry;
import io.sentry.SentryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Handles stacktraces, sends it to Sentry.io
 */
public class IStacktraceHandler implements StacktraceHandler {

    /**
     * The Sentry.io client.
     */
    private final SentryClient client;

    /**
     * The {@link LegendaryBot} instance of the bot
     */
    private LegendaryBot bot;

    /**
     * The Logger
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    public IStacktraceHandler(LegendaryBot bot, String sentryKey) {
        this.bot = bot;
        client = Sentry.init(sentryKey);
    }

    @Override
    public void sendStacktrace(Throwable e, String... tags) {
        Arrays.stream(tags).forEach((v) -> client.addTag(v.split(":")[0], v.split(":")[1]));
        client.sendException(e);
        client.setTags(null);
        log.info("Sent error to Sentry");
    }
}
