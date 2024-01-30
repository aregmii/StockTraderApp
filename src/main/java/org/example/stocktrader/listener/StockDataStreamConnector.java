package org.example.stocktrader.listener;

import lombok.SneakyThrows;
import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.realtime.enums.MarketDataMessageType;
import net.jacobpeterson.alpaca.websocket.marketdata.MarketDataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class StockDataStreamConnector {
    private static final Logger logger = LoggerFactory.getLogger(StockDataStreamConnector.class);
    private final AlpacaAPI alpacaAPI;

    @Autowired
    public StockDataStreamConnector(final AlpacaAPI alpacaAPI,
                                    @Value("${alpaca.websocket.threadpool.size}") final int threadPoolSize) {
        this.alpacaAPI = alpacaAPI;
        logger.info("AlpacaWebSocketClient initialized with thread-pool size: {}", threadPoolSize);
    }

    public void subscribe(MarketDataListener marketDataListener, List<String> symbols) throws RuntimeException {
        try {
            if (alpacaAPI == null || alpacaAPI.stockMarketDataStreaming() == null) {
                throw new IllegalStateException("alpacaAPI is not initialized");
            }
            logger.info("Connecting and subscribing to symbols: {}", symbols);

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
            alpacaAPI.stockMarketDataStreaming().subscribe(symbols, symbols, symbols);

        } catch (Exception ex) {
            logger.error("Failed to subscribe to symbols", ex);
            throw new RuntimeException("Unexpected error while subscribing", ex);
        }
    }

    @SneakyThrows
    public void keepWebSocketOpen() {
        try {
            // Keep the thread open for 15 seconds
            Thread.sleep(3);
        } catch (InterruptedException e) {
            logger.error("Thread interrupted in keepWebSocketOpen: ", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interruption while keeping thread open", e);
        }
    }
}