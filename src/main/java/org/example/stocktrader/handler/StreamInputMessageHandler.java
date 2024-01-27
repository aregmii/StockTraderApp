package org.example.stocktrader.handler;

import java.time.Instant;

public interface StreamInputMessageHandler<T> {
    void handleStreamInput(final T stockMessage, final Instant timestamp);
}
