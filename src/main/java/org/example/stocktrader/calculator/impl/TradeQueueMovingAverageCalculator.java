package org.example.stocktrader.calculator.impl;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.realtime.enums.MarketDataMessageType;
import org.example.stocktrader.calculator.QueueMovingAverageCalculator;

import java.util.HashMap;
import java.util.Map;

/**
 * The TradeQueueMovingAverageCalculator class implements the QueueMovingAverageCalculator interface
 * and provides a method to calculate the moving average of a collection of SttockTradeMessages.
 */
public class TradeQueueMovingAverageCalculator implements QueueMovingAverageCalculator {
    public static Map<MarketDataMessageType, TradeQueueMovingAverageCalculator> validators = new HashMap<>();

    @Override
    public double calculateMovingAverage(final Map data) {
        //TODO -
        return 0;
    }
}
