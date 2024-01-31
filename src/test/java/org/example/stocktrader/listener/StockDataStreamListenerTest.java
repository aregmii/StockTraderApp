package org.example.stocktrader.listener;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.endpoint.clock.Clock;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.realtime.enums.MarketDataMessageType;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.realtime.bar.StockBarMessage;
import net.jacobpeterson.alpaca.rest.endpoint.clock.ClockEndpoint;
import org.example.stocktrader.calculator.QueueMovingAverageCalculator;
import org.example.stocktrader.calculator.QueueMovingAverageCalculatorRegistry;
import org.example.stocktrader.publisher.StreamInputMessagePublisher;
import org.example.stocktrader.publisher.impl.BarStreamInputMessagePublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import static org.mockito.Mockito.*;

@SpringBootTest
public class StockDataStreamListenerTest {

    @Mock
    private AlpacaAPI alpacaAPI;

    @Mock
    private Executor executorService;

    @Mock
    private List<StreamInputMessagePublisher<?>> handlers;

    @Mock
    private BarStreamInputMessagePublisher barStreamInputMessageHandler;

    @Mock
    private Clock clock;

    @Mock
    private ClockEndpoint clockEndpoint;

    @Mock
    QueueMovingAverageCalculator queueMovingAverageCalculator;

    private StockDataStreamListener stockDataStreamListener;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        stockDataStreamListener = new StockDataStreamListener(
                alpacaAPI,
                5,
                executorService
        );
    }

    @Test
    @DisplayName("Testing StockDataStreamListener connectAndSubscribe method when the market is open")
    public void testConnectAndSubscribeWhenMarketOpen() throws Exception {
        mockClock(true);

        // Preparing the Singleton for the test
        QueueMovingAverageCalculatorRegistry.validators.put(MarketDataMessageType.BAR, queueMovingAverageCalculator);

        stockDataStreamListener.connectAndSubscribe(Collections.singletonList("APPL"));

        verify(clock, times(1)).getIsOpen();
        verify(barStreamInputMessageHandler, never()).handleStreamInput(any(StockBarMessage.class), any(Instant.class));
    }

    @Test
    @DisplayName("Testing StockDataStreamListener connectAndSubscribe method when the market is closed")
    public void testConnectAndSubscribeWhenMarketClosed() throws Exception {
        mockClock(false);

        // Preparing the Singleton for the test
        QueueMovingAverageCalculatorRegistry.validators.put(MarketDataMessageType.BAR, queueMovingAverageCalculator);

        stockDataStreamListener.connectAndSubscribe(Collections.singletonList("APPL"));

        verify(clock, times(1)).getIsOpen();
    }

    private void mockClock(boolean isOpen) throws Exception {
        when(clock.getIsOpen()).thenReturn(isOpen);
        when(alpacaAPI.clock()).thenReturn(clockEndpoint);
        when(clockEndpoint.get()).thenReturn(clock);
    }
}