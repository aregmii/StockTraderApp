package org.example.stocktrader.facades;

import org.example.stocktrader.client.AlpacaWebSocketClient;
import net.jacobpeterson.alpaca.AlpacaAPI;

import java.util.List;

public class StockDataFacade {

    private final AlpacaWebSocketClient alpacaWebSocketClient;

    public StockDataFacade(final AlpacaAPI alpacaAPI) {
        this.alpacaWebSocketClient = new AlpacaWebSocketClient(alpacaAPI);
    }

    public void startStreaming(final List<String> symbol) throws InterruptedException {
        alpacaWebSocketClient.connectAndSubscribe(symbol);
    }
}