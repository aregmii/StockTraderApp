package org.example.stocktrader.handler.impl;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.realtime.enums.MarketDataMessageType;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.realtime.bar.StockBarMessage;
import org.example.stocktrader.manager.BarMessageQueueManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BarStreamInputMessageHandlerTest {

    @Mock
    private BarMessageQueueManager barMessageQueueManager;

    private BarStreamInputMessageHandler handler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new BarStreamInputMessageHandler(barMessageQueueManager);
    }

    @Test
    public void testCanHandle() {
        assertTrue(handler.canHandle(MarketDataMessageType.BAR));
        assertFalse(handler.canHandle(MarketDataMessageType.TRADE));
    }

    @Test
    public void testHandleStreamInput() {
        StockBarMessage msg = new StockBarMessage();

        handler.handleStreamInput(msg, Instant.now());

        verify(barMessageQueueManager).publish(msg);
    }

    @Test
    public void testHandleStreamInputExceptionHandling() {
        StockBarMessage msg = new StockBarMessage();
        RuntimeException exception = new RuntimeException("test");

        // Tell that when 'publish' method of 'barMessageQueueManager' gets called with any 'StockBarMessage' instance
        // Then throw a new RuntimeException
        doThrow(exception).when(barMessageQueueManager).publish(any(StockBarMessage.class));

        // We expect 'handleStreamInput' method to complete without throwing exception (because it catches them)
        handler.handleStreamInput(msg, Instant.now());

        // And we verify that 'publish' method of 'barMessageQueueManager' indeed gets called as a result
        verify(barMessageQueueManager).publish(msg);
    }}