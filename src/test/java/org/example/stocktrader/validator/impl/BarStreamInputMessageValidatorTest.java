package org.example.stocktrader.validator.impl;

import org.example.stocktrader.exception.StreamInputValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.assertThrows;

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