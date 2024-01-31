package org.example.stocktrader;

import org.example.stocktrader.facades.AlpacaFacade;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;


public class StockTraderApplicationTest {

    @Mock
    private AlpacaFacade alpacaFacadeMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInit() {
        StockTraderApplication stockTraderApplication = new StockTraderApplication(alpacaFacadeMock, "AAPL");
        doNothing().when(alpacaFacadeMock).startStreaming(anyList());
        stockTraderApplication.init();

        verify(alpacaFacadeMock, times(1)).startStreaming(anyList());
    }
}
