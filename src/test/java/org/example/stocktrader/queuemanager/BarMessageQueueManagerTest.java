package org.example.stocktrader.queuemanager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.realtime.bar.BarMessage;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class BarMessageQueueManagerTest {

    @Mock
    private Map<String, Queue<BarMessage>> barMessagesInMemoryMapExcludingTarget;
    
    @Mock
    private Map<String, Queue<BarMessage>> barMessageInMemoryMapTargetStock;
    
    @InjectMocks
    private BarMessageQueueManager barMessageQueueManager;

    private BarMessage barMessage;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        barMessagesInMemoryMapExcludingTarget = new HashMap<>();
        barMessageInMemoryMapTargetStock = new HashMap<>();
        barMessage = new BarMessage();

    }

    @Test
    public void testPublish() {
        barMessage.setSymbol("ABC");
        barMessage.setTimestamp(ZonedDateTime.now());
        
        Queue<BarMessage> targetQueue = new LinkedList<>();
        targetQueue.add(barMessage);
        
        barMessagesInMemoryMapExcludingTarget.put("ABC", targetQueue);
        barMessageQueueManager.execute(barMessage);
       
        verify(barMessagesInMemoryMapExcludingTarget).putIfAbsent(any(), any());
    }

    // Additional test cases should be created to test other method functionalities
    @Test
    public void testExecute_withTargetStock() {
        Map<String, Queue<BarMessage>> testMapTarget = new HashMap<>();
        Map<String, Queue<BarMessage>> testMapExcludingTarget = new HashMap<>();
        BarMessageQueueManager manager = new BarMessageQueueManager(testMapExcludingTarget, testMapTarget);
        BarMessage testBarMessage = createMockBarMessage("AAPL");

        when(testBarMessage.getSymbol()).thenReturn("AAPL");
        manager.execute(testBarMessage);

        verify(barMessage, times(1)).getSymbol();
    }

    @Test
    public void testExecute_withOtherStock() {
        Map<String, Queue<BarMessage>> testMapTarget = new HashMap<>();
        Map<String, Queue<BarMessage>> testMapExcludingTarget = new HashMap<>();

        BarMessageQueueManager manager = new BarMessageQueueManager(testMapExcludingTarget, testMapTarget);
        BarMessage testBarMessage = createMockBarMessage("TSLA");

        when(testBarMessage.getSymbol()).thenReturn("TSLA");
        manager.execute(testBarMessage);

        verify(barMessage, times(1)).getSymbol();
    }

    private BarMessage createMockBarMessage(String symbol){
        BarMessage barMessage = mock(BarMessage.class);
        when(barMessage.getTimestamp()).thenReturn(ZonedDateTime.now());
        when(barMessage.getSymbol()).thenReturn(symbol);
        return barMessage;
    }
}