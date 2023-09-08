package com.progsa.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StockListingService {
    @Value("${alphaVantage.apiKey}")
    private String alphaVantageApiKey;

    @Value("${finnhub.apiKey}")
    private String finnhubApiKey;

    private final RestTemplate restTemplate;

    public StockListingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

//    public String getStockQuote(String symbol) {
//        String finnhubApiUrl = "https://finnhub.io/api/v1/quote?symbol=" + symbol +
//                "&token=" + finnhubApiKey;
//
//        return restTemplate.getForObject(finnhubApiUrl, String.class);
//    }
//    public String tickerSearch(String keyword) {
//        String apiUrl = "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=" + keyword +
//                "&apikey=" + alphaVantageApiKey;
//
//        return restTemplate.getForObject(apiUrl, String.class);
//    }
//    public String getPriceHistory(String symbol) {
//        String apiUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=" + symbol +
//                "&apikey=" + alphaVantageApiKey;
//
//        return restTemplate.getForObject(apiUrl, String.class);
//    }

    public ResponseEntity<String> getStockPrice(String symbol) {
        String finnhubApiUrl = "https://finnhub.io/api/v1/quote?symbol=" + symbol +
                "&token=" + finnhubApiKey;

        try {
            String response = restTemplate.getForObject(finnhubApiUrl, String.class);

            // Process the JSON response to extract only the required fields
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);

            double currentPrice = rootNode.get("c").asDouble();
            double high = rootNode.get("h").asDouble();
            double low = rootNode.get("l").asDouble();
            double open = rootNode.get("o").asDouble();
            double priceChangePercentage = (currentPrice - open) / open * 100;

            // Create a map to store the extracted data
            Map<String, Object> stockQuoteData = new HashMap<>();
            stockQuoteData.put("currentPrice", currentPrice);
            stockQuoteData.put("high", high);
            stockQuoteData.put("low", low);
            stockQuoteData.put("open", open);
            stockQuoteData.put("priceChangePercentage", priceChangePercentage);

            // Convert the extracted data to a JSON string
            String processedResponse = objectMapper.writeValueAsString(stockQuoteData);

            return ResponseEntity.ok(processedResponse);
        } catch (Exception e) {
            // Handle the exception and return an error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching the latest stock value.");
        }
    }

    public ResponseEntity<String> tickerSearch(String keyword) {
        String apiUrl = "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=" + keyword +
                "&apikey=" + alphaVantageApiKey;

        try {
            String response = restTemplate.getForObject(apiUrl, String.class);

            // Process the JSON response to extract "symbol" and "name" fields
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode bestMatches = rootNode.get("bestMatches");

            List<String> symbolsAndNames = new ArrayList<>();
            for (int i = 0; i < Math.min(bestMatches.size(), 10); i++) {
                JsonNode match = bestMatches.get(i);
                String symbol = match.get("1. symbol").asText();
                String name = match.get("2. name").asText();
                symbolsAndNames.add("Symbol: " + symbol + ", Name: " + name);
            }

            // Convert the extracted data to a JSON array string
            String processedResponse = objectMapper.writeValueAsString(symbolsAndNames);
            log.info(processedResponse);
            return ResponseEntity.ok(processedResponse);
        } catch (Exception e) {
            // Handle the exception and return an error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while performing ticker search on service layer.");
        }
    }

    public ResponseEntity<String> getPriceHistory(String symbol) {
        String apiUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=" + symbol +
                "&apikey=" + alphaVantageApiKey;

        try {
            String response = restTemplate.getForObject(apiUrl, String.class);

            // Parse the JSON response using Jackson
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode timeSeries = rootNode.get("Time Series (Daily)");

            // Create a JSON object to store the first 7 entries
            ObjectNode first7Entries = objectMapper.createObjectNode();
            Iterator<Map.Entry<String, JsonNode>> entries = timeSeries.fields();
            int entryCount = 0;
            while (entries.hasNext() && entryCount < 7) {
                Map.Entry<String, JsonNode> entry = entries.next();
                first7Entries.set(entry.getKey(), entry.getValue());
                entryCount++;
            }

            // Convert the processed JSON back to a string
            String processedResponse = objectMapper.writeValueAsString(first7Entries);

            return ResponseEntity.ok(processedResponse);
        } catch (Exception e) {
            // Handle the exception and return an error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching price history.");
        }
    }
}
