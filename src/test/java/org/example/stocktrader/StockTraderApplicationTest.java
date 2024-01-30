package org.example.stocktrader;

import org.example.stocktrader.facades.StockDataFacade;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;


public class StockTraderApplicationTest {

    @Mock
    private StockDataFacade stockDataFacadeMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInit() {
        StockTraderApplication stockTraderApplication = new StockTraderApplication(stockDataFacadeMock, "AAPL");
        doNothing().when(stockDataFacadeMock).startStreaming(anyList());
        stockTraderApplication.init();

        verify(stockDataFacadeMock, times(1)).startStreaming(anyList());
    }
}
