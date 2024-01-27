package org.example.stocktrader.client;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.realtime.enums.MarketDataMessageType;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.realtime.bar.StockBarMessage;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.realtime.quote.StockQuoteMessage;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.realtime.trade.StockTradeMessage;
import net.jacobpeterson.alpaca.websocket.marketdata.MarketDataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class AlpacaWebSocketClient {

    private final AlpacaAPI alpacaAPI;
    private final ExecutorService executorService;
    private static final Logger logger = LoggerFactory.getLogger(AlpacaWebSocketClient.class);

    @Autowired
    public AlpacaWebSocketClient(AlpacaAPI alpacaAPI, @Value("${alpaca.websocket.threadpool.size}") int threadPoolSize) {
        this.alpacaAPI = alpacaAPI;
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
        logger.info("AlpacaWebSocketClient initialized with thread-pool size: {}", threadPoolSize);
    }

    public void connectAndSubscribe(List<String> symbols) {
        try {
            logger.info("Connecting and subscribing to symbols: {}", symbols);
            MarketDataListener marketDataListener = this::handleMarketData;
            alpacaAPI.stockMarketDataStreaming().setListener(marketDataListener);

            alpacaAPI.stockMarketDataStreaming().subscribeToControl(
                    MarketDataMessageType.SUCCESS,
                    MarketDataMessageType.SUBSCRIPTION,
                    MarketDataMessageType.ERROR,
                    MarketDataMessageType.BAR,
                    MarketDataMessageType.QUOTE,
                    MarketDataMessageType.TRADE);

            alpacaAPI.stockMarketDataStreaming().connect();
            if (!alpacaAPI.stockMarketDataStreaming().waitForAuthorization(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Websocket authorization failed!");
            }

            if (!alpacaAPI.stockMarketDataStreaming().isValid()) {
                throw new IllegalStateException("Websocket connection is not valid!");
            }

            logger.info("Websocket connected and authorized successfully.");

            // Subscribe to the specified symbols
            alpacaAPI.stockMarketDataStreaming().subscribe(symbols, symbols, symbols);

            keepWebSocketOpen();

        } catch (Exception e) {
            logger.error("Error in connectAndSubscribe: ", e);
            // Implement appropriate error handling/recovery strategy
        }
    }

    private void handleMarketData(MarketDataMessageType messageType, Object message) {
        executorService.submit(() -> processMarketData(messageType, message));
    }

    private void processMarketData(MarketDataMessageType messageType, Object message) {
        try {
            Instant timestamp = Instant.now();
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
                    logger.info("[{}] Unknown message type: {}", timestamp, messageType);
            }
        } catch (Exception e) {
            logger.error("Error processing market data: ", e);
            // Decide on a recovery strategy (e.g., logging, alerting, retrying)
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

    private void keepWebSocketOpen() {
        try {
            // Keep the WebSocket connection open (adjust time as needed)
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            logger.error("Thread interrupted in keepWebSocketOpen: ", e);
            Thread.currentThread().interrupt();
        } finally {
            alpacaAPI.stockMarketDataStreaming().disconnect();
            logger.info("Websocket disconnected.");
        }
    }
}
