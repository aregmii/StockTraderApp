package org.example.stocktrader.listener;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.endpoint.clock.Clock;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.realtime.enums.MarketDataMessageType;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.realtime.bar.StockBarMessage;
import net.jacobpeterson.alpaca.websocket.marketdata.MarketDataListener;
import org.example.stocktrader.handler.StreamInputMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

@Component
public class StockDataStreamListener {
    private static final Logger logger = LoggerFactory.getLogger(StockDataStreamListener.class);
    private final AlpacaAPI alpacaAPI;
    private final Executor executorService;

    @Autowired
    private List<StreamInputMessageHandler<?>> handlers;

    @Autowired
    public StockDataStreamListener(final AlpacaAPI alpacaAPI,
                                   @Value("${alpaca.websocket.threadpool.size}") final int threadPoolSize,
                                   @Qualifier("taskExecutor") final Executor executorService) {
        this.alpacaAPI = alpacaAPI;
        this.executorService = executorService;
        logger.info("AlpacaWebSocketClient initialized with thread-pool size: {}", threadPoolSize);
    }

    public void connectAndSubscribe(final List<String> symbols) {
        try {

            // Check Market Clock
            Clock clock = alpacaAPI.clock().get();
            logger.info("AlpacaAPI clocl details: {}", clock.toString());
            boolean isMarketOpen = clock.getIsOpen();

            if (isMarketOpen) {
                logger.info("Market is open. Listening to live stream from Alpaca");
                connectToAlpacaSocketStream(symbols);
            } else {
                logger.info("Market is closed. Mocking stream expected from Alpaca");
                connectToMockStream(symbols);
            }

        } catch (final Exception e) {
            logger.error("Error in connectAndSubscribe: ", e);
            //Connection re-attempt strategy.
        }
    }

    private void connectToMockStream(final List<String> symbols) {
        ScheduledExecutorService mockStreamExecutor = Executors.newSingleThreadScheduledExecutor();
        mockStreamExecutor.scheduleAtFixedRate(() -> {
            Instant timestamp = Instant.now();

            // Mocking a Bar message
            symbols.forEach(symbol -> {
                StockBarMessage barMessage = new StockBarMessage();
                barMessage.setSymbol(symbol);
                barMessage.setOpen((double) Instant.now().toEpochMilli()); // Mock data
                barMessage.setClose((double) Instant.now().toEpochMilli() + 3); // Mock data
                barMessage.setHigh((double) Instant.now().toEpochMilli() + 30); // Mock data
                barMessage.setLow((double) Instant.now().toEpochMilli() - 15); // Mock data
                barMessage.setTimestamp(ZonedDateTime.now());
                barMessage.setVolume(1000L); // Mock data

            });

            /*

            // Mocking a Trade message
            symbols.forEach(symbol -> {
                StockTradeMessage tradeMessage = new StockTradeMessage();
                tradeMessage.setSymbol(symbol);
                tradeMessage.setPrice(102.5); // Mock data
                tradeMessage.setSize(10); // Mock data
                tradeStreamInputMessageHandler.handleStreamInput(tradeMessage, timestamp);
            });

            // Mocking a Quote message
            symbols.forEach(symbol -> {
                StockQuoteMessage quoteMessage = new StockQuoteMessage();
                quoteMessage.setSymbol(symbol);
                quoteMessage.setBidPrice(101.0); // Mock data
                quoteMessage.setAskPrice(103.0); // Mock data
                quoteMessage.setBidSize(500); // Mock data
                quoteMessage.setAskSize(500); // Mock data
                quoteStreamInputMessageHandler.handleStreamInput(quoteMessage, timestamp);
            }); */

        }, 0, 1, TimeUnit.SECONDS); // Adjust the period to suit your testing needs
    }


    private void connectToAlpacaSocketStream(final List<String> symbols) {
        logger.info("Connecting and subscribing to symbols: {}", symbols);
        MarketDataListener marketDataListener = this::processMarketData;
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
    }

    private void processMarketData(final MarketDataMessageType messageType, final Object message) {
        try {
            Instant timestamp = Instant.now();
            Optional<? extends StreamInputMessageHandler<?>> optionalHandler = handlers.stream()
                    .filter(h -> h.canHandle(messageType))
                    .findFirst();
            if (optionalHandler.isPresent()) {
                handleStreamInputHelper(optionalHandler.get(), message, timestamp);
            } else {
                logger.info("[{}] No handler found for Unknown message type: {}", timestamp, messageType);
            }
        } catch (Exception e) {
            logger.error("Error processing market data: ", e);
            // TODO: Decide on a recovery strategy (e.g., logging, alerting, retrying). Initially, send to DLQ
            // TODO: Generate a discrepancy report to send to DLQ with all relevant event and failure metadata details.
        }
    }

    private <T> void handleStreamInputHelper(StreamInputMessageHandler<T> handler, Object message, Instant timestamp) {
        T typedMessage = (T) message;
        handler.handleStreamInput(typedMessage, timestamp);
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