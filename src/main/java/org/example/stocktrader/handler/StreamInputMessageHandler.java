package org.example.stocktrader.handler;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.realtime.enums.MarketDataMessageType;

import java.time.Instant;

public interface StreamInputMessageHandler<T> {
    void handleStreamInput(final T stockMessage, final Instant timestamp);

    boolean canHandle(MarketDataMessageType messageType);
}
