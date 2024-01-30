package org.example.stocktrader.publisher.impl;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.realtime.enums.MarketDataMessageType;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.realtime.bar.StockBarMessage;
import org.example.stocktrader.publisher.StreamInputMessagePublisher;
import org.example.stocktrader.queuemanager.BarMessageQueueManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class BarStreamInputMessagePublisher implements StreamInputMessagePublisher<StockBarMessage> {
    private static final Logger logger = LoggerFactory.getLogger(BarStreamInputMessagePublisher.class);

    private  final BarMessageQueueManager barMessageQueueManager;

    @Autowired
    public BarStreamInputMessagePublisher(BarMessageQueueManager barMessageQueueManager) {
        this.barMessageQueueManager = barMessageQueueManager;
    }

    @Override
    public void handleStreamInput(StockBarMessage message, Instant timestamp) {
            try {
            logger.info("[{}] Received Bar: Symbol: {}, Open: {}, Close: {}, High: {}, Low: {}",
                    timestamp, message.getSymbol(), message.getOpen(), message.getClose(),
                    message.getHigh(), message.getLow());

            logger.info("Publishing the bar message to the Queue manager");

            barMessageQueueManager.execute(message);
        } catch (Exception e) {
            logger.error("Error processing bar message", e);
            // Handle the exception as required
        }
    }

    @Override
    public boolean canHandle(MarketDataMessageType messageType) {
        return messageType == MarketDataMessageType.BAR;
    }

}
