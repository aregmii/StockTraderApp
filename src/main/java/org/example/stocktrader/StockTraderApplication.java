package org.example.stocktrader;

import org.example.stocktrader.facades.StockDataFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@SpringBootApplication
@EnableAsync
public class StockTraderApplication {
    private final StockDataFacade stockDataFacade;

    @Autowired
    public StockTraderApplication(final StockDataFacade stockDataFacade) {
        this.stockDataFacade = stockDataFacade;
    }

    public static void main(String[] args) {
        SpringApplication.run(StockTraderApplication.class, args);
    }

    @PostConstruct
    public void init() {
        System.out.println("StockTrader Application Started.");
        stockDataFacade.startStreaming(Arrays.asList("AMZN", "AAPL"));
    }
}
