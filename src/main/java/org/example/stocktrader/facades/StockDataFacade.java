package org.example.stocktrader.facades;

import org.example.stocktrader.listener.StockDataStreamListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockDataFacade {

    private final StockDataStreamListener stockDataStreamListener;

    @Autowired
    public StockDataFacade(final StockDataStreamListener stockDataStreamListener) {
        this.stockDataStreamListener = stockDataStreamListener;
    }

    public void startStreaming(final List<String> symbols) {
        stockDataStreamListener.connectAndSubscribe(symbols);
    }
}
