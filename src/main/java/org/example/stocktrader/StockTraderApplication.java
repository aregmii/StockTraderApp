package org.example.stocktrader;

import org.example.stocktrader.facades.StockDataFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@SpringBootApplication
public class StockTraderApplication {

    private final StockDataFacade stockDataFacade;

    @Autowired
    public StockTraderApplication(StockDataFacade stockDataFacade) {
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
