package org.example.stocktrader;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.properties.DataAPIType;
import net.jacobpeterson.alpaca.model.properties.EndpointAPIType;
import org.example.stocktrader.facades.StockDataFacade;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

public class StockTraderApplication {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("StockTrader Application Started.");

        Properties properties = new Properties();
        try (InputStream input = StockTraderApplication.class
                .getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new IllegalStateException("Unable to find application.properties");
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new IllegalStateException("Error reading application.properties", ex);
        }

        String keyId = properties.getProperty("alpaca.keyId");
        String secretKey = properties.getProperty("alpaca.secretKey");

        AlpacaAPI alpacaAPI = AlpacaAPI.builder()
                .withEndpointAPIType(EndpointAPIType.PAPER)
                .withKeyID(keyId)
                .withSecretKey(secretKey)
                .withDataAPIType(DataAPIType.SIP)
                .build();

        System.out.println("alpacaAPI initialized");

        StockDataFacade stockDataFacade = new StockDataFacade(alpacaAPI);

        System.out.println("stockDataFacade initialized");

        stockDataFacade.startStreaming(Arrays.asList("AMZN", "AAPL"));
    }
}
