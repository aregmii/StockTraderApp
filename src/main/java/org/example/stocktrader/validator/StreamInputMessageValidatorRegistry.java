package org.example.stocktrader.validator;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.realtime.enums.MarketDataMessageType;
import org.example.stocktrader.validator.impl.BarStreamInputMessageValidator;
import org.example.stocktrader.validator.impl.QuoteStreamInputMessageValidator;
import org.example.stocktrader.validator.impl.TradeStreamInputMessageValidator;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class StreamInputMessageValidatorRegistry {
    public static Map<MarketDataMessageType, StreamInputMessageValidator> validators = new HashMap<>();

    StreamInputMessageValidatorRegistry() {
        validators.put(MarketDataMessageType.BAR, new BarStreamInputMessageValidator());
        validators.put(MarketDataMessageType.QUOTE, new QuoteStreamInputMessageValidator());
        validators.put(MarketDataMessageType.TRADE, new TradeStreamInputMessageValidator());
    }

}
