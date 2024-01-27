package org.example.stocktrader.handler.impl;

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
    public void handleStreamInput(final StockTradeMessage tradeMessage, final Instant timestamp) {
        logger.info("[{}] Received Trade: Symbol: {}, Price: {}, Size: {}, Timestamp: {}",
                timestamp, tradeMessage.getSymbol(), tradeMessage.getPrice(), tradeMessage.getSize(), tradeMessage.getTimestamp());
        /*
        [OkHttp https://stream.data.alpaca.markets/...] INFO org.example.stocktrader.client.AlpacaWebSocketClient - [2024-01-26T15:44:28.468Z]
        Received Trade: Symbol: TSLA, Price: 184.994, Size: 1, Timestamp: 2024-01-26T15:44:28.458177790Z
        */


    }
}
