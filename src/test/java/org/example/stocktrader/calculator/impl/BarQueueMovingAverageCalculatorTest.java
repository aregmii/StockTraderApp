package org.example.stocktrader.calculator.impl;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.realtime.bar.StockBarMessage;
import org.example.stocktrader.calculator.QueueMovingAverageCalculator;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BarQueueMovingAverageCalculatorTest {

    @Test
    void calculateMovingAverage_allPricesSame_checkAverage() {
        // Arrange
        BarQueueMovingAverageCalculator calculator = new BarQueueMovingAverageCalculator();
        Map<String, Queue<StockBarMessage>> barMessagesMap = new HashMap<>();
        Queue<StockBarMessage> barMessageQueue = new LinkedList<>();
        barMessagesMap.put("test", barMessageQueue);
        for (int i = 0; i < 5; i++) {
            StockBarMessage message = new StockBarMessage();
            message.setClose(10.0);
            barMessageQueue.add(message);
        }
        
        // Act
        double result = calculator.calculateMovingAverage(barMessagesMap);

        // Assert
        assertEquals(10.0, result, "The calculated average is incorrect.");
    }

    @Test
    void calculateMovingAverage_differentPrices_checkAverage() {
        // Arrange
        BarQueueMovingAverageCalculator calculator = new BarQueueMovingAverageCalculator();
        Map<String, Queue<StockBarMessage>> barMessagesMap = new HashMap<>();
        Queue<StockBarMessage> barMessageQueue = new LinkedList<>();
        barMessagesMap.put("test", barMessageQueue);

        double[] prices = {10.0, 20.0, 30.0, 40.0, 50.0};
        for (double price : prices) {
            StockBarMessage message = new StockBarMessage();
            message.setClose(price);
            barMessageQueue.add(message);
        }

        // Act
        double result = calculator.calculateMovingAverage(barMessagesMap);

        // Assert
        assertEquals(30.0, result, "The calculated average is incorrect.");
    }

    @Test
    void calculateMovingAverage_emptyMap_checkAverage() {
        // Arrange
        BarQueueMovingAverageCalculator calculator = new BarQueueMovingAverageCalculator();
        Map<String, Queue<StockBarMessage>> barMessagesMap = new HashMap<>();

        // Act
        double result = calculator.calculateMovingAverage(barMessagesMap);

        // Assert
        assertEquals(0.0, result, "The calculated average is incorrect.");
    }
}