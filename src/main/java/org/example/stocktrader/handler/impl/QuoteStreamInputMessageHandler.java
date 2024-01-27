package org.example.stocktrader.handler.impl;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.realtime.quote.StockQuoteMessage;
import org.example.stocktrader.handler.StreamInputMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class QuoteStreamInputMessageHandler implements StreamInputMessageHandler<StockQuoteMessage> {
    private static final Logger logger = LoggerFactory.getLogger(QuoteStreamInputMessageHandler.class);

    @Override
    public void handleStreamInput(final StockQuoteMessage quoteMessage, final Instant timestamp) {
        logger.info("[{}] Received Quote: Symbol: {}, Bid Price: {}, Ask Price: {}, Bid Size: {}, Ask Size: {}",
                timestamp, quoteMessage.getSymbol(), quoteMessage.getBidPrice(), quoteMessage.getAskPrice(),
                quoteMessage.getBidSize(), quoteMessage.getAskSize());
        /*
        OkHttp https://stream.data.alpaca.markets/...] INFO org.example.stocktrader.client.AlpacaWebSocketClient - [2024-01-26T15:44:28.722Z]
        Received Quote: Symbol: AMZN, Bid Price: 158.91, Ask Price: 158.93, Bid Size: 3, Ask Size: 5
        */

    }
}
