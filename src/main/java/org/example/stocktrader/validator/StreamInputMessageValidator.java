package org.example.stocktrader.validator;

public interface StreamInputMessageValidator<T> {
    boolean validate(final T message);
}
