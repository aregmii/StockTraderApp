package org.example.stocktrader.client;

import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class SubscriptionRequestBuilder {
    private List<String> symbols = new ArrayList<>();
    @SuppressFBWarnings("URF_UNREAD_FIELD")
    private String streamType = "trade_updates";

    public SubscriptionRequestBuilder addSymbol(String symbol) {
        symbols.add(symbol);
        return this;
    }

    public SubscriptionRequestBuilder setStreamType(String streamType) {
        this.streamType = streamType;
        return this;
    }

    public String build() {
        // Convert symbols and stream type to the required JSON format
        return "{\"action\":\"subscribe\",\"trades\":" + symbols.toString() + "}";
    }
}