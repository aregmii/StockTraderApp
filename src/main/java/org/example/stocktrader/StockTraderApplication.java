package org.example.stocktrader;

import org.example.stocktrader.facades.AlpacaFacade;
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
    private final AlpacaFacade alpacaFacade;

    private final List<String> subscriptionStockList;

    @Autowired
    public StockTraderApplication(final AlpacaFacade alpacaFacade,
                                  final @Value("${subscription.stocks}") String stocks) {
        this.alpacaFacade = alpacaFacade;
        this.subscriptionStockList = Arrays.asList(stocks.split(","));
    }

    public static void main(String[] args) {
        SpringApplication.run(StockTraderApplication.class, args);
    }

    @PostConstruct
    public void init() {
        System.out.println("StockTrader Application Started.");
        alpacaFacade.startStreaming(subscriptionStockList);
    }
}
