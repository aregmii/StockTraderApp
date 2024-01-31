package org.example.stocktrader.publisher;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.realtime.enums.MarketDataMessageType;

import java.time.Instant;

/**
 * Interface for a Stream Input Message Publisher.
 * This interface provides methods to handle stream input messages and check if the publisher can handle a certain message type.
 *
 * @param <T> the type of the stream input message
 */
public interface StreamInputMessagePublisher<T> {
    void handleStreamInput(final T stockMessage, final Instant timestamp);

    boolean canHandle(MarketDataMessageType messageType);
}
