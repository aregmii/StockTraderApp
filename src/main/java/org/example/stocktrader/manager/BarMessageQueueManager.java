package org.example.stocktrader.manager;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.realtime.bar.BarMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

@Component
public class BarMessageQueueManager {
    private static final Logger logger = LoggerFactory.getLogger(BarMessageQueueManager.class);
    private static final long TWENTY_MINUTES_IN_MILLIS = 20 * 60 * 1000;
    private final Map<String, Queue<BarMessage>> BAR_MESSAGES_IN_MEMORY_MAP;

    @Value("${target.stock}")
    private String targetStock;

    public BarMessageQueueManager() {
        logger.info("Initializing BarMessageQueueManager");
        this.BAR_MESSAGES_IN_MEMORY_MAP = new HashMap<>();
    }

    // Additional constructor for testing
    public BarMessageQueueManager(Map<String, Queue<BarMessage>> barMessagesInMemoryMap) {
        logger.info("Initializing BarMessageQueueManager with provided map");
        this.BAR_MESSAGES_IN_MEMORY_MAP = barMessagesInMemoryMap;
    }

    public synchronized void publish(final BarMessage barMessage) {
        String symbol = barMessage.getSymbol();
        BAR_MESSAGES_IN_MEMORY_MAP.putIfAbsent(symbol, new LinkedList<>());
        Queue<BarMessage> queue = BAR_MESSAGES_IN_MEMORY_MAP.get(symbol);
        queue.add(barMessage);
        logger.info("Bar message added to queue for symbol: {}", symbol);

        cleanupOldMessages(queue, barMessage.getTimestamp().toInstant().toEpochMilli());
        checkBuyCondition();
    }

    private void cleanupOldMessages(Queue<BarMessage> queue, final long currentTimestamp) {
        while (!queue.isEmpty()
                &&
                currentTimestamp - queue
                        .peek()
                        .getTimestamp()
                        .toInstant()
                        .toEpochMilli()
                        > TWENTY_MINUTES_IN_MILLIS) {
            queue.poll();
        }
    }

    private void checkBuyCondition() {
        double overallAverage = calculateOverallAverage();
        double aaplAverage = calculateSymbolAverage(targetStock);

        logger.info("Overall average: {}", overallAverage);
        logger.info("AAPL average: {}", aaplAverage);

        if (overallAverage > someThreshold() && aaplAverage > someThreshold()) {
            //Additional logic. At minimum, there should be X number of records in the queue that helped calculate the average
            logger.info("BUY CONDITION MET: Overall Average: {}, AAPL Average: {}", overallAverage, aaplAverage);
        } else {
            logger.info("BUY CONDITION NOT MET");
        }
    }

    private double calculateOverallAverage() {
        return BAR_MESSAGES_IN_MEMORY_MAP.values().stream()
                .flatMap(Queue::stream)
                .mapToDouble(BarMessage::getClose)
                .average()
                .orElse(0);
    }

    private double calculateSymbolAverage(final String symbol) {
        Queue<BarMessage> barMessages = BAR_MESSAGES_IN_MEMORY_MAP.getOrDefault(symbol, new LinkedList<>());
        return barMessages.stream()
                .mapToDouble(BarMessage::getClose)
                .average()
                .orElse(0);
    }

    private double someThreshold() {
        return 100;
    }
}
