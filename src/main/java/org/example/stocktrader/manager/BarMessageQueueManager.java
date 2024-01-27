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
        this.BAR_MESSAGES_IN_MEMORY_MAP = new HashMap<>();
    }

    public void publish(final BarMessage barMessage) {
        try {
            logger.info("BarMessageQueueManager received publish request for message: {}", barMessage.toString());
            logger.info("Current BAR_MESSAGES_IN_MEMORY_MAP is: {}", BAR_MESSAGES_IN_MEMORY_MAP);
            BAR_MESSAGES_IN_MEMORY_MAP.putIfAbsent(barMessage.getSymbol(), new LinkedList<>());
            Queue<BarMessage> queue = BAR_MESSAGES_IN_MEMORY_MAP.get(barMessage.getSymbol());
            queue.add(barMessage);
            logger.info("Current BAR_MESSAGES_IN_MEMORY_MAP after adding the recent published bar message: {}", BAR_MESSAGES_IN_MEMORY_MAP);
            cleanupOldMessages(queue, barMessage.getTimestamp().toInstant().toEpochMilli());
            logger.info("Current BAR_MESSAGES_IN_MEMORY_MAP updated to: {}", BAR_MESSAGES_IN_MEMORY_MAP);
            checkBuyCondition();
        } catch (Exception ex) {
            logger.error("Exception in publish method");
            logger.error(ex.getMessage());
        }
    }

    private void cleanupOldMessages(Queue<BarMessage> queue, final long currentTimestamp) {
        logger.info("CleanUpOldMessages invoked");
        while (!queue.isEmpty() && currentTimestamp - queue.peek().getTimestamp().toInstant().toEpochMilli() > TWO_MINUTES_IN_MILLIS) {
            queue.poll();
            logger.info("Polled queue");
        }
    }

    private void checkBuyCondition() {
        double overallAverage = BAR_MESSAGES_IN_MEMORY_MAP.values().stream()
                .flatMap(Queue::stream)
                .mapToDouble(BarMessage::getClose)
                .average()
                .orElse(0);

        logger.info("Overall average is: {}", overallAverage);

        double aaplAverage = BAR_MESSAGES_IN_MEMORY_MAP.getOrDefault("AAPL", new LinkedList<>())
                .stream()
                .mapToDouble(BarMessage::getClose)
                .average()
                .orElse(0);

        logger.info("AAPL average is: {}", aaplAverage);

        if (overallAverage > 0 && aaplAverage > 0) {
            System.out.println("BUY APPL");
        } else {
            logger.info("DONT BUY APPL");
        }
    }
}
