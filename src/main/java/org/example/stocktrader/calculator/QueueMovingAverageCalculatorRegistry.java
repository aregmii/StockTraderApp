package org.example.stocktrader.calculator;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.realtime.enums.MarketDataMessageType;
import org.example.stocktrader.calculator.impl.BarQueueMovingAverageCalculator;
import org.example.stocktrader.calculator.impl.QuoteQueueMovingAverageCalculator;
import org.example.stocktrader.calculator.impl.TradeQueueMovingAverageCalculator;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * The QueueMovingAverageCalculatorRegistry class is responsible for registering different types of
 * QueueMovingAverageCalculator implementations based on MarketDataMessageType.
 */
@Component
public class QueueMovingAverageCalculatorRegistry {
    public static Map<MarketDataMessageType, QueueMovingAverageCalculator> validators = new HashMap<>();

    QueueMovingAverageCalculatorRegistry() {
        validators.put(MarketDataMessageType.BAR, new BarQueueMovingAverageCalculator());
        validators.put(MarketDataMessageType.QUOTE, new QuoteQueueMovingAverageCalculator());
        validators.put(MarketDataMessageType.TRADE, new TradeQueueMovingAverageCalculator());
    }
}
