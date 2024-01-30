package org.example.stocktrader.validator.impl;

import org.example.stocktrader.exception.StreamInputValidationException;
import org.example.stocktrader.publisher.impl.BarStreamInputMessagePublisher;
import org.example.stocktrader.validator.StreamInputMessageValidator;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.realtime.bar.StockBarMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class BarStreamInputMessageValidator implements StreamInputMessageValidator<StockBarMessage> {
    private static final Logger logger = LoggerFactory.getLogger(BarStreamInputMessagePublisher.class);
    @Override
    public boolean validate(final StockBarMessage message) {
        if (Objects.isNull(message) || Objects.isNull(message.getClose())) {
            throw new StreamInputValidationException("Received StockBarMessage is invalid:" + message);
        }
        // For now, assume all validations pass successfully
        logger.info("Successful validation of StockBarMessage for message: {}", message);
        return true;
    }
}
