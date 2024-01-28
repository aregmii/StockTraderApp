package org.example.stocktrader.manager;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.realtime.bar.BarMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

@Component
public class BarMessageQueueManager {
    private static final Logger logger = LoggerFactory.getLogger(BarMessageQueueManager.class);
    private static final long TWO_MINUTES_IN_MILLIS = 2 * 60 * 1000;
    private final Map<String, Queue<BarMessage>> BAR_MESSAGES_IN_MEMORY_MAP;

    public BarMessageQueueManager() {
        logger.info("Initializing BarMessageQueueManager");
        this.BAR_MESSAGES_IN_MEMORY_MAP = new HashMap<>();
    }

    public synchronized void publish(final BarMessage barMessage) {
        logger.info("Entering method: publish");
        logger.info("Input parameter barMessage: {}", barMessage);
        String symbol = barMessage.getSymbol();
        BAR_MESSAGES_IN_MEMORY_MAP.putIfAbsent(symbol, new LinkedList<>());
        Queue<BarMessage> queue = BAR_MESSAGES_IN_MEMORY_MAP.get(symbol);
        queue.add(barMessage);
        logger.info("Bar message added to queue for symbol: {}", symbol);

        cleanupOldMessages(queue, barMessage.getTimestamp().toInstant().toEpochMilli());
        checkBuyCondition(symbol);
        logger.info("Exiting method: publish");
    }

    private void cleanupOldMessages(Queue<BarMessage> queue, final long currentTimestamp) {
        logger.debug("Entering method: cleanupOldMessages");
        while (!queue.isEmpty() && currentTimestamp - queue.peek().getTimestamp().toInstant().toEpochMilli() > TWO_MINUTES_IN_MILLIS) {
            BarMessage removedMessage = queue.poll();
            logger.info("Removed old message: {}", removedMessage);
        }
        logger.info("Exiting method: cleanupOldMessages");
    }

    private void checkBuyCondition(String symbol) {
        logger.info("Entering method: checkBuyCondition");
        logger.info("Checking condition for symbol: {}", symbol);
        double overallAverage = calculateOverallAverage();
        double aaplAverage = calculateSymbolAverage("AAPL");

        logger.info("Overall average: {}", overallAverage);
        logger.info("AAPL average: {}", aaplAverage);

        if (overallAverage > someThreshold() && aaplAverage > someThreshold()) {
            logger.info("BUY CONDITION MET: Overall Average: {}, AAPL Average: {}", overallAverage, aaplAverage);
        } else {
            logger.info("BUY CONDITION NOT MET");
        }
        logger.info("Exiting method: checkBuyCondition");
    }

    private double calculateOverallAverage() {
        logger.info("Entering method: calculateOverallAverage");
        double average = BAR_MESSAGES_IN_MEMORY_MAP.values().stream()
                .flatMap(Queue::stream)
                .mapToDouble(BarMessage::getClose)
                .average()
                .orElse(0);
        logger.info("Overall average calculated: {}", average);
        logger.info("Exiting method: calculateOverallAverage");
        return average;
    }

    private double calculateSymbolAverage(String symbol) {
        logger.info("Entering method: calculateSymbolAverage");
        logger.info("Calculating average for symbol: {}", symbol);
        Queue<BarMessage> barMessages = BAR_MESSAGES_IN_MEMORY_MAP.getOrDefault(symbol, new LinkedList<>());
        double average = barMessages.stream()
                .mapToDouble(BarMessage::getClose)
                .average()
                .orElse(0);
        logger.info("Symbol average calculated: {}", average);
        logger.info("Exiting method: calculateSymbolAverage");
        return average;
    }

    private double someThreshold() {
        logger.info("Entering method: someThreshold");
        double threshold = 100; // Example threshold, adjust based on your requirement
        logger.info("Threshold value: {}", threshold);
        logger.info("Exiting method: someThreshold");
        return threshold;
    }
}
