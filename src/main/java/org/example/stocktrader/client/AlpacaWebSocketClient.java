package org.example.stocktrader.client;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.realtime.enums.MarketDataMessageType;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.realtime.bar.StockBarMessage;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.realtime.quote.StockQuoteMessage;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.realtime.trade.StockTradeMessage;
import net.jacobpeterson.alpaca.websocket.marketdata.MarketDataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AlpacaWebSocketClient {

    private final AlpacaAPI alpacaAPI;
    private static final Logger logger = LoggerFactory.getLogger(AlpacaWebSocketClient.class);

    public AlpacaWebSocketClient(AlpacaAPI alpacaAPI) {
        this.alpacaAPI = alpacaAPI;
        logger.info("AlpacaWebSocketClient constructor completed.");
    }

    public void connectAndSubscribe(List<String> symbols) {
        try {
            logger.info("connectAndSubscribe started for symbols: {}", symbols);

            MarketDataListener marketDataListener = this::handleMarketData;

            alpacaAPI.stockMarketDataStreaming().setListener(marketDataListener);
            alpacaAPI.stockMarketDataStreaming().subscribeToControl(
                    MarketDataMessageType.SUCCESS,
                    MarketDataMessageType.SUBSCRIPTION,
                    MarketDataMessageType.ERROR);

            alpacaAPI.stockMarketDataStreaming().connect();
            if (!alpacaAPI.stockMarketDataStreaming().waitForAuthorization(5, TimeUnit.SECONDS)) {
                logger.error("Websocket authorization failed!");
                return;
            }

            if (!alpacaAPI.stockMarketDataStreaming().isValid()) {
                logger.error("Websocket not valid!");
                return;
            }

            logger.info("Websocket successfully connected and authorized.");

            // Subscribe to trades, quotes, and bars for the symbols
            alpacaAPI.stockMarketDataStreaming().subscribe(
                    new ArrayList<>(symbols), // Trades
                    new ArrayList<>(symbols), // Quotes
                    new ArrayList<>(symbols)); // Bars

            keepWebSocketOpen();

        } catch (Exception e) {
            logger.error("Exception in connectAndSubscribe: ", e);
        } finally {
            alpacaAPI.stockMarketDataStreaming().disconnect();
            logger.info("Websocket disconnected.");
        }
    }

    private void handleMarketData(MarketDataMessageType messageType, Object message) {
        Instant timestamp = Instant.now();
        logger.info("Received streaming message with messageType: {}, message: {}, timestamp: {}", messageType, message, timestamp);

        switch (messageType) {
            case BAR:
                if (message instanceof StockBarMessage) {
                    handleBarMessage((StockBarMessage) message, timestamp);
                }
                break;
            case TRADE:
                if (message instanceof StockTradeMessage) {
                    handleTradeMessage((StockTradeMessage) message, timestamp);
                }
                break;
            case QUOTE:
                if (message instanceof StockQuoteMessage) {
                    handleQuoteMessage((StockQuoteMessage) message, timestamp);
                }
                break;
            default:
                logger.info("[{}] {}: {}", timestamp, messageType.name(), messageToString(message));
                break;
        }
    }

    private void handleBarMessage(StockBarMessage barMessage, Instant timestamp) {
        logger.info("[{}] Received Bar: Symbol: {}, Open: {}, Close: {}, High: {}, Low: {}, Volume: {}",
                timestamp, barMessage.getSymbol(), barMessage.getOpen(), barMessage.getClose(),
                barMessage.getHigh(), barMessage.getLow(), barMessage.getVolume());
    }

    private void handleTradeMessage(StockTradeMessage tradeMessage, Instant timestamp) {
        logger.info("[{}] Received Trade: Symbol: {}, Price: {}, Size: {}, Timestamp: {}",
                timestamp, tradeMessage.getSymbol(), tradeMessage.getPrice(), tradeMessage.getSize(), tradeMessage.getTimestamp());
    }

    private void handleQuoteMessage(StockQuoteMessage quoteMessage, Instant timestamp) {
        logger.info("[{}] Received Quote: Symbol: {}, Bid Price: {}, Ask Price: {}, Bid Size: {}, Ask Size: {}",
                timestamp, quoteMessage.getSymbol(), quoteMessage.getBidPrice(), quoteMessage.getAskPrice(),
                quoteMessage.getBidSize(), quoteMessage.getAskSize());
    }

    private static String messageToString(Object message) {
        StringBuilder result = new StringBuilder();
        try {
            for (Field field : message.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                result.append(field.getName()).append(": ").append(field.get(message)).append(", ");
            }
        } catch (IllegalAccessException e) {
            return "Error accessing fields";
        }
        return result.length() > 0 ? result.substring(0, result.length() - 2) : "No fields found"; // remove trailing comma and space
    }

    private void keepWebSocketOpen() throws InterruptedException {
        while (!Thread.currentThread().isInterrupted()) {
            TimeUnit.SECONDS.sleep(1);
        }
    }
}