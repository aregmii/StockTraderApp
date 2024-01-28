package org.example.stocktrader;

import org.example.stocktrader.facades.StockDataFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableAsync
public class StockTraderApplication {
    private final StockDataFacade stockDataFacade;

    private final List<String> subscriptionStockList;

    @Autowired
    public StockTraderApplication(final StockDataFacade stockDataFacade,
                                  final @Value("${subscription.stocks}") String stocks) {
        this.stockDataFacade = stockDataFacade;
        this.subscriptionStockList = Arrays.asList(stocks.split(","));
    }

    public static void main(String[] args) {
        SpringApplication.run(StockTraderApplication.class, args);
    }

    @PostConstruct
    public void init() {
        System.out.println("StockTrader Application Started.");
        stockDataFacade.startStreaming(subscriptionStockList);
    }
}
