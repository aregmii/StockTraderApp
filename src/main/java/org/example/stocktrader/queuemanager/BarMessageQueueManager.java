package org.example.stocktrader.queuemanager;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.realtime.bar.BarMessage;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.realtime.enums.MarketDataMessageType;
import org.example.stocktrader.calculator.QueueMovingAverageCalculator;
import org.example.stocktrader.calculator.QueueMovingAverageCalculatorRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.*;

/**
 * The BarMessageQueueManager class is responsible for managing queues of BarMessage objects.
 * It provides methods to publish bar messages to the appropriate queues, cleanup expired messages,
 * and check a buy condition based on the moving averages of the queues.
 */
@Component
public class BarMessageQueueManager {
    private static final Logger logger = LoggerFactory.getLogger(BarMessageQueueManager.class);
    private static final long TWENTY_MINUTES_IN_MILLIS = 20 * 60 * 1000;
    private final Map<String, Queue<BarMessage>> BAR_MESSAGES_IN_MEMORY_MAP_EXCLUDING_TARGET;

    private final Map<String, Queue<BarMessage>> BAR_MESSAGES_IN_MEMORY_MAP_TARGET;

    @Value("${target.stock}")
    private String targetStock;

    @Autowired
    QueueMovingAverageCalculatorRegistry queueMovingAverageCalculatorRegistry;

    public BarMessageQueueManager() {
        logger.info("Initializing BarMessageQueueManager");
        this.BAR_MESSAGES_IN_MEMORY_MAP_EXCLUDING_TARGET = new HashMap<>();
        this.BAR_MESSAGES_IN_MEMORY_MAP_TARGET = new HashMap<>();
    }

    // Additional constructor for testing
    public BarMessageQueueManager(Map<String, Queue<BarMessage>> barMessagesInMemoryMapExceptTarget,
                                  Map<String, Queue<BarMessage>> barMessageInMemoryTargetStock) {
        logger.info("Initializing BarMessageQueueManager with provided map");
        this.BAR_MESSAGES_IN_MEMORY_MAP_EXCLUDING_TARGET = barMessagesInMemoryMapExceptTarget;
        this.BAR_MESSAGES_IN_MEMORY_MAP_TARGET = barMessageInMemoryTargetStock;
    }

    public synchronized void execute(final BarMessage barMessage) {
        Queue<BarMessage> targetQueue = null;
        String symbol = barMessage.getSymbol();

        if (barMessage.getSymbol().equals(targetStock)) {
            targetQueue = manageTargetQueue(barMessage, targetQueue, symbol, BAR_MESSAGES_IN_MEMORY_MAP_TARGET);
        } else {
            targetQueue = manageExcludingTargetQueue(barMessage, targetQueue, symbol, BAR_MESSAGES_IN_MEMORY_MAP_EXCLUDING_TARGET);
        }

        if(targetQueue != null) {
            cleanupOldMessages(targetQueue, barMessage.getTimestamp().toInstant().toEpochMilli());
            checkBuyCondition();
        }
    }

    private Queue<BarMessage> manageTargetQueue(final BarMessage barMessage, Queue<BarMessage> targetQueue, String symbol, Map<String, Queue<BarMessage>> memoryMap) {
        return manageBarMessageQueue(barMessage, targetQueue, symbol, memoryMap);
    }

    private Queue<BarMessage> manageExcludingTargetQueue(final BarMessage barMessage, Queue<BarMessage> targetQueue, String symbol, Map<String, Queue<BarMessage>> memoryMap) {

        return manageBarMessageQueue(barMessage, targetQueue, symbol, memoryMap);
    }

    private Queue<BarMessage> manageBarMessageQueue(BarMessage barMessage, Queue<BarMessage> targetQueue, String symbol, Map<String, Queue<BarMessage>> memoryMap) {
        ZonedDateTime earliestMessageInMemoryQueue = getOldestTimestamp(memoryMap);
        ZonedDateTime barMessageTimestamp = barMessage.getTimestamp();

        if (earliestMessageInMemoryQueue == null || barMessageTimestamp.isAfter(earliestMessageInMemoryQueue)) {
            targetQueue = addToQueueAndGet(barMessage, symbol, memoryMap);
            logger.info("Bar message added to target queue for symbol: {}", symbol);
            logger.info("Updated queue is: {}", targetQueue);
        } else {
            logger.warn("Received barMessage: {} for symbol: {} later than expected. BarMessage timestamp is: {}" +
                    "and queue earliest entry timestamp is: {}", barMessage, targetStock,barMessageTimestamp, earliestMessageInMemoryQueue);
        }
        return targetQueue;
    }

    private Queue<BarMessage> addToQueueAndGet(final BarMessage barMessage, String symbol, Map<String, Queue<BarMessage>> memoryMap) {
        memoryMap.putIfAbsent(symbol, new LinkedList<>());
        Queue<BarMessage> targetQueue = memoryMap.get(symbol);
        targetQueue.add(barMessage);

        return targetQueue;
    }

    private void cleanupOldMessages(Queue<BarMessage> queue, final long currentTimestamp) {
        BarMessage barMessage;
        while (!queue.isEmpty()
                && (barMessage = queue.peek()) != null
                && barMessage.getTimestamp() != null
                && currentTimestamp - barMessage.getTimestamp().toInstant().toEpochMilli() > TWENTY_MINUTES_IN_MILLIS) {
            queue.poll();
        }
    }

    private void checkBuyCondition() {
        double overallMovingAverage = calculateAverage(BAR_MESSAGES_IN_MEMORY_MAP_EXCLUDING_TARGET);
        double targetStockMovingAvg = calculateAverage(BAR_MESSAGES_IN_MEMORY_MAP_TARGET);

        logger.info("Overall average: {}", overallMovingAverage);
        logger.info("target stock average: {}", targetStockMovingAvg);

        Optional<BarMessage> firstMessageInAvgQueue =  extractEarliestQueueMessage(BAR_MESSAGES_IN_MEMORY_MAP_EXCLUDING_TARGET);
        Optional<BarMessage> firstMessageInTargetQueue = extractEarliestQueueMessage(BAR_MESSAGES_IN_MEMORY_MAP_TARGET);

        //averages of both queues should be greater than the first entry in the queue
        if (firstMessageInAvgQueue.isPresent() && firstMessageInTargetQueue.isPresent() &&
                overallMovingAverage > firstMessageInAvgQueue.get().getClose() &&
                targetStockMovingAvg > firstMessageInTargetQueue.get().getClose()
        ) {
            logger.info("BUY CONDITION MET: Overall Average: {}, targetStock: {} Average: {}", overallMovingAverage, targetStock, targetStockMovingAvg);
        } else {
            logger.info("BUY CONDITION NOT MET");
        }
    }

    private double calculateAverage(Map<String, Queue<BarMessage>> barMessagesMap) {
        QueueMovingAverageCalculator calculator =
                QueueMovingAverageCalculatorRegistry.validators.get(MarketDataMessageType.BAR);
        if (calculator == null) {
            throw new IllegalStateException("No calculator registered for messageType: barMessage");
        }

        return calculator.calculateMovingAverage(barMessagesMap);
    }

    private static ZonedDateTime getOldestTimestamp(Map<String, Queue<BarMessage>> map) {
        return map.values().stream()
                .flatMap(Collection::stream)
                .map(BarMessage::getTimestamp)
                .min(ZonedDateTime::compareTo)
                .orElse(null);
    }

    private Optional<BarMessage> extractEarliestQueueMessage(Map<String, Queue<BarMessage>> map) {
        return map.values().stream()
                .filter(queue -> !queue.isEmpty()) // Filter out empty queues
                .map(Queue::peek)  // Retrieves head of this queue, or returns null if this queue is empty.
                .findFirst();  // Get the first element
    }
}
