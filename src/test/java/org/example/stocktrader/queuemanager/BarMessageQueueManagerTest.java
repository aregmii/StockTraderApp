package org.example.stocktrader.queuemanager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

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

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        barMessagesInMemoryMapExcludingTarget = new HashMap<>();
        barMessageInMemoryMapTargetStock = new HashMap<>();
    }

    @Test
    public void testPublish() {
        BarMessage barMessage = new BarMessage();
        barMessage.setSymbol("ABC");
        barMessage.setTimestamp(ZonedDateTime.now());
        
        Queue<BarMessage> targetQueue = new LinkedList<>();
        targetQueue.add(barMessage);
        
        barMessagesInMemoryMapExcludingTarget.put("ABC", targetQueue);
        barMessageQueueManager.execute(barMessage);
       
        verify(barMessagesInMemoryMapExcludingTarget).putIfAbsent(any(), any());
    }

    // Additional test cases should be created to test other method functionalities
}