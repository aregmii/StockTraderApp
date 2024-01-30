package org.example.stocktrader.validator.impl;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.realtime.quote.StockQuoteMessage;
import org.example.stocktrader.exception.StreamInputValidationException;
import org.example.stocktrader.validator.StreamInputMessageValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class QuoteStreamInputMessageValidator implements StreamInputMessageValidator<StockQuoteMessage> {
    private static final Logger logger = LoggerFactory.getLogger(QuoteStreamInputMessageValidator.class);

    @Override
    public boolean validate(final StockQuoteMessage message) {
        // Validation logic
        logger.info("Successful validation of StockQuoteMessage: {} ", message);
        return true;
    }
}
