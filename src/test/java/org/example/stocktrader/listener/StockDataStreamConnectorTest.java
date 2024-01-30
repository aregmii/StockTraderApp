package org.example.stocktrader.listener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.websocket.marketdata.MarketDataListener;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class StockDataStreamConnectorTest {
    @Mock
    private AlpacaAPI alpacaAPI;

    @Mock
    private MarketDataListener marketDataListener;

    private StockDataStreamConnector stockDataStreamConnector;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        stockDataStreamConnector = new StockDataStreamConnector(alpacaAPI, 1);
    }

    /**
     * Tests the subscription of StockDataStreamConnector to symbols
     */
    @Test
    public void subscribeTest(){
        try {
            String expectedSymbol = "AAPL";

            stockDataStreamConnector.subscribe(marketDataListener, Arrays.asList(expectedSymbol));

            verify(alpacaAPI.stockMarketDataStreaming()).subscribe(eq(Arrays.asList(expectedSymbol)), eq(Arrays.asList(expectedSymbol)), eq(Arrays.asList(expectedSymbol)));
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test 'keepWebSocketOpen' method of StockDataStream class.
     */
    @Test
    public void keepWebSocketOpen() {
        try {
            stockDataStreamConnector.keepWebSocketOpen();
            verify(alpacaAPI.stockMarketDataStreaming()).disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}