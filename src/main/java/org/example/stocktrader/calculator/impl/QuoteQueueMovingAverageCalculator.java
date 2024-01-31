package org.example.stocktrader.calculator.impl;


import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.realtime.quote.StockQuoteMessage;
import org.example.stocktrader.calculator.QueueMovingAverageCalculator;

import java.util.Map;
import java.util.Queue;

/**
 * The QuoteQueueMovingAverageCalculator class implements the QueueMovingAverageCalculator interface
 * and provides a method to calculate the moving average of a collection of StockQuoteMessage.
 */
public class QuoteQueueMovingAverageCalculator implements QueueMovingAverageCalculator<StockQuoteMessage> {
    @Override
    public double calculateMovingAverage(final Map<String, Queue<StockQuoteMessage>> data) {
        //Todo
        return 0;
    }
}