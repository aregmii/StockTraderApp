package org.example.stocktrader.facades;

import org.example.stocktrader.client.AlpacaWebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockDataFacade {

    private final AlpacaWebSocketClient alpacaWebSocketClient;

    @Autowired
    public StockDataFacade(AlpacaWebSocketClient alpacaWebSocketClient) {
        this.alpacaWebSocketClient = alpacaWebSocketClient;
    }

    public void startStreaming(final List<String> symbols) {
        alpacaWebSocketClient.connectAndSubscribe(symbols);
    }
}
