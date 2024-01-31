package org.example.stocktrader.calculator.impl;


import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.realtime.bar.StockBarMessage;
import org.example.stocktrader.calculator.QueueMovingAverageCalculator;

import java.util.Map;
import java.util.Queue;

public class BarQueueMovingAverageCalculator implements QueueMovingAverageCalculator<StockBarMessage> {
    @Override
    public double calculateMovingAverage(final Map<String, Queue<StockBarMessage>> barMessagesMap) {
        int count = 0;
        double sum = 0.0;

        for (Queue<StockBarMessage> queue : barMessagesMap.values()) {
            for (StockBarMessage message : queue) {
                sum += message.getClose();
                count++;
            }
        }
        return (count == 0) ? 0 : sum / count;
    }
}