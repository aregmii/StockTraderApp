package org.example.stocktrader.handler.impl;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.realtime.enums.MarketDataMessageType;
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
    public void handleStreamInput(StockQuoteMessage stockMessage, Instant timestamp) {
        logger.debug("[{}] Received Quote: Symbol: {}, Bid Price: {}, Ask Price: {}, Bid Size: {}, Ask Size: {}",
                timestamp, stockMessage.getSymbol(), stockMessage.getBidPrice(), stockMessage.getAskPrice(),
                stockMessage.getBidSize(), stockMessage.getAskSize());

    }

    @Override
    public boolean canHandle(MarketDataMessageType messageType) {
        return messageType == MarketDataMessageType.QUOTE;
    }

}
