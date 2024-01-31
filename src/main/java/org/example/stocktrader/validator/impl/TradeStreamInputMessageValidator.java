package org.example.stocktrader.validator.impl;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.realtime.trade.StockTradeMessage;
import org.example.stocktrader.exception.StreamInputValidationException;
import org.example.stocktrader.validator.StreamInputMessageValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TradeStreamInputMessageValidator implements StreamInputMessageValidator<StockTradeMessage> {
    private static final Logger logger = LoggerFactory.getLogger(TradeStreamInputMessageValidator.class);

    @Override
    public boolean validate(final StockTradeMessage message) {
        logger.debug("Successful validation of StockTradeMessage: {} ", message);
        return true;
    }
}
