package org.example.stocktrader.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncTestService {

    private static final Logger logger = LoggerFactory.getLogger(AsyncTestService.class);

    @Async
    public void executeAsyncTask() {
        logger.info("Executing asynchronous task in thread: " + Thread.currentThread().getName());
        // Additional logic for the async task can be added here
    }
}