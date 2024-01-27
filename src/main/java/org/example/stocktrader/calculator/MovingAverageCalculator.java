package org.example.stocktrader.calculator;

import java.util.Queue;

public interface MovingAverageCalculator <T>{

    //calculate moving average
    double calculateMovingAveraage(final Queue<T> messages);
}
