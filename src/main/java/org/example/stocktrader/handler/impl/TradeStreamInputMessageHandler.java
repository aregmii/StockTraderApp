package org.example.stocktrader.handler.impl;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.realtime.enums.MarketDataMessageType;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.realtime.trade.StockTradeMessage;
import org.example.stocktrader.handler.StreamInputMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TradeStreamInputMessageHandler implements StreamInputMessageHandler<StockTradeMessage> {
    private static final Logger logger = LoggerFactory.getLogger(TradeStreamInputMessageHandler.class);

    @Override
    public void handleStreamInput(StockTradeMessage stockMessage, Instant timestamp) {
        logger.debug("[{}] Received Trade: Symbol: {}, Price: {}, Size: {}, Timestamp: {}",
                timestamp, stockMessage.getSymbol(), stockMessage.getPrice(), stockMessage.getSize(), stockMessage.getTimestamp());

    }

    @Override
    public boolean canHandle(MarketDataMessageType messageType) {
        return messageType == MarketDataMessageType.TRADE;
    }
}
