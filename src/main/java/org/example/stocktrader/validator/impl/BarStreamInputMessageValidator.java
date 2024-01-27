package org.example.stocktrader.validator.impl;

import org.example.stocktrader.exception.ValidationException;
import org.example.stocktrader.handler.impl.BarStreamInputMessageHandler;
import org.example.stocktrader.validator.StreamInputMessageValidator;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.realtime.bar.StockBarMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BarStreamInputMessageValidator implements StreamInputMessageValidator<StockBarMessage> {
    private static final Logger logger = LoggerFactory.getLogger(BarStreamInputMessageHandler.class);
    @Override
    public void validate(final StockBarMessage message) throws ValidationException {
        // For now, assume all validations pass successfully
        logger.info("Successful validation of StockBarMessage for message: {}", message);
    }
}
