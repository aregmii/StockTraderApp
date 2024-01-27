package org.example.stocktrader.validator;

import org.example.stocktrader.exception.ValidationException;

public interface StreamInputMessageValidator<T> {
    void validate(final T message) throws ValidationException;
}
