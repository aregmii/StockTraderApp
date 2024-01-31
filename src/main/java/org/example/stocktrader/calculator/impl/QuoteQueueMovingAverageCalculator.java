package org.example.stocktrader.calculator.impl;


import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.realtime.quote.StockQuoteMessage;
import org.example.stocktrader.calculator.QueueMovingAverageCalculator;

import java.util.Map;
import java.util.Queue;

public class QuoteQueueMovingAverageCalculator implements QueueMovingAverageCalculator<StockQuoteMessage> {
    @Override
    public double calculateMovingAverage(final Map<String, Queue<StockQuoteMessage>> data) {
        //Todo
        return 0;
    }
}