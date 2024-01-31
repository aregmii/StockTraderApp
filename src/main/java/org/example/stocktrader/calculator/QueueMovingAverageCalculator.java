package org.example.stocktrader.calculator;

import java.util.Map;
import java.util.Queue;

/**
 * The QueueMovingAverageCalculator interface provides a method to calculate the moving average of a collection of data.
 * The data is represented as a map where the key is a string and the value is a queue of type T.
 */
public interface QueueMovingAverageCalculator<T>{

    //calculate moving average
    double calculateMovingAverage(final Map<String, Queue<T>> data);
}
