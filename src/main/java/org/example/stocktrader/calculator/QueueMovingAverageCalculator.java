package org.example.stocktrader.calculator;

import java.util.Map;
import java.util.Queue;

public interface QueueMovingAverageCalculator<T>{

    //calculate moving average
    double calculateMovingAverage(final Map<String, Queue<T>> data);
}
