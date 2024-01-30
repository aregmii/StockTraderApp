package org.example.stocktrader.validator.impl;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.realtime.bar.StockBarMessage;
import org.example.stocktrader.exception.StreamInputValidationException;
import org.example.stocktrader.validator.StreamInputMessageValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class BarStreamInputMessageValidatorTest {

    @InjectMocks
    private BarStreamInputMessageValidator barStreamInputMessageValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void validate_validationsFail_throwsStreamInputValidationException() {
        assertThrows(StreamInputValidationException.class, () -> barStreamInputMessageValidator.validate(null));
    }
}