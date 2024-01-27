package org.example.stocktrader.config;

import net.jacobpeterson.alpaca.AlpacaAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlpacaConfig {

    @Value("${alpaca.api.key}")
    private String apiKey;

    @Value("${alpaca.secret.key}")
    private String secretKey;

    @Bean
    public AlpacaAPI alpacaAPI() {
        return new AlpacaAPI(apiKey, secretKey);
    }
}