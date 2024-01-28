package org.example.stocktrader.handler.impl;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.realtime.bar.StockBarMessage;
import org.example.stocktrader.handler.StreamInputMessageHandler;
import org.example.stocktrader.manager.BarMessageQueueManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class BarStreamInputMessageHandler implements StreamInputMessageHandler<StockBarMessage> {
    private static final Logger logger = LoggerFactory.getLogger(BarStreamInputMessageHandler.class);

    private  final BarMessageQueueManager barMessageQueueManager;

    @Autowired
    public BarStreamInputMessageHandler(BarMessageQueueManager barMessageQueueManager) {
        this.barMessageQueueManager = barMessageQueueManager;
    }

    @Override
    @Async
    public void handleStreamInput(final StockBarMessage barMessage, final Instant timestamp) {
        logger.info("[{}] Received Bar: Symbol: {}, Open: {}, Close: {}, High: {}, Low: {}, Volume: {}",
                timestamp, barMessage.getSymbol(), barMessage.getOpen(), barMessage.getClose(),
                barMessage.getHigh(), barMessage.getLow(), barMessage.getVolume());

        logger.info("Publishing the bar message to the Queue manager");

        barMessageQueueManager.publish(barMessage);

        /*
        [OkHttp https://stream.data.alpaca.markets/...] INFO org.example.stocktrader.client.AlpacaWebSocketClient - [2024-01-26T14:22:00.056Z]
        Received Bar: Symbol: AMZN, Open: 158.11, Close: 158.24, High: 158.24, Low: 158.11, Volume: 1671
        */
    }
}
