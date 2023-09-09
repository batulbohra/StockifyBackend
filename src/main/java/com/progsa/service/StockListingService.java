package com.progsa.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.progsa.IOModels.StockDetailInputModel;
import com.progsa.IOModels.StockDetailOutputModel;
import com.progsa.dao.PortfolioDao;
import com.progsa.dao.TransactionDao;
import com.progsa.model.PortfolioEntity;
import org.apache.coyote.Response;
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
    private final PortfolioDao portfolioDao;
    private final TransactionDao transactionDao;

    public StockListingService(RestTemplate restTemplate, PortfolioDao portfolioDao, TransactionDao transactionDao) {
        this.restTemplate = restTemplate;
        this.portfolioDao = portfolioDao;
        this.transactionDao = transactionDao;
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
        try {
            JsonNode rootNode = getCurrentPrice(symbol);

            //Extract from JSON response
            double currentPrice = rootNode.get("c").asDouble();
            double high = rootNode.get("h").asDouble();
            double low = rootNode.get("l").asDouble();
            double open = rootNode.get("o").asDouble();
            double priceChangePercentage = rootNode.get("dp").asDouble();

            // Create a map to store the extracted data
            Map<String, Object> stockQuoteData = new HashMap<>();
            stockQuoteData.put("currentPrice", currentPrice);
            stockQuoteData.put("high", high);
            stockQuoteData.put("low", low);
            stockQuoteData.put("open", open);
            stockQuoteData.put("priceChangePercentage", priceChangePercentage);

            // Convert the extracted data to a JSON string
            ObjectMapper objectMapper = new ObjectMapper();
            String processedResponse = objectMapper.writeValueAsString(stockQuoteData);

            return ResponseEntity.ok(processedResponse);
        } catch (Exception e) {
            // Handle the exception and return an error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching the latest stock value.");
        }
    }

    public JsonNode getCurrentPrice(String symbol) {
        log.info("C");
        String finnhubApiUrl = "https://finnhub.io/api/v1/quote?symbol=" + symbol +
                "&token=" + finnhubApiKey;
        log.info("Called");
        String response = restTemplate.getForObject(finnhubApiUrl, String.class);
        log.info(response);
        // Process the JSON response
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = objectMapper.readTree(response);
        } catch (JsonProcessingException e) {
            log.error("JSON processing error");
            throw new RuntimeException(e);
        }
        return rootNode;
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

            ArrayNode simplifiedOutput = JsonNodeFactory.instance.arrayNode();
            int count=1;
            for (JsonNode matchNode : bestMatches) {
                ObjectNode simplifiedMatch = JsonNodeFactory.instance.objectNode();
                simplifiedMatch.put("name", matchNode.get("2. name").asText());
                simplifiedMatch.put("symbol", matchNode.get("1. symbol").asText());
                simplifiedOutput.add(simplifiedMatch);
                count++;
                if (count==10) break;
            }

            // Convert the extracted data to a JSON array string
            String processedResponse = objectMapper.writeValueAsString(simplifiedOutput);
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

    public ResponseEntity<StockDetailOutputModel> getStockDetail(StockDetailInputModel stockDetailInput){
        try {
            PortfolioEntity portfolioEntity = portfolioDao.findByEmailAndSymbol(
                    stockDetailInput.getEmail(), stockDetailInput.getSymbol());

            double price = getCurrentPrice(stockDetailInput.getSymbol()).get("c").asDouble();

            if (portfolioEntity==null){
                return ResponseEntity.ok(new StockDetailOutputModel(stockDetailInput.getEmail(),
                        stockDetailInput.getSymbol(), 0, 0, price, 0));
            }
             double netProfitLoss = transactionDao.calculateNetStockProfitLoss(
                    stockDetailInput.getEmail(), stockDetailInput.getSymbol());

            StockDetailOutputModel stockDetailOutputModel = new StockDetailOutputModel(portfolioEntity.getEmail(),
                    portfolioEntity.getSymbol(), netProfitLoss, portfolioEntity.getVolume(), price,
                    portfolioEntity.getVolume()*price);
            return ResponseEntity.ok(stockDetailOutputModel);
        } catch(Exception e){
            return ResponseEntity.ok(null);
        }
    }
}
