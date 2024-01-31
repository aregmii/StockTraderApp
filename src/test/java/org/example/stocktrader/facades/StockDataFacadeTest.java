package org.example.stocktrader.facades;

import org.example.stocktrader.listener.StockDataStreamListener;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

public class StockDataFacadeTest {

    private final StockDataStreamListener listener = Mockito.mock(StockDataStreamListener.class);

    /**
     * This test case tests the startStreaming method of StockDataFacade class.
     */
    @Test
    public void testStartStreaming() {
        // Initialize the object to be tested with mock dependencies
        StockDataFacade stockDataFacade = new StockDataFacade(listener);
        List<String> symbols = Arrays.asList("AAPL", "GOOGL", "MSFT");

        // Call the method to be tested
        stockDataFacade.startStreaming(symbols);

        // Verify the interaction with the listener
        Mockito.verify(listener, Mockito.times(1)).connectAndSubscribe(symbols);
    }
}