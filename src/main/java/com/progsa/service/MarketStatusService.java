package com.progsa.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class MarketStatusService {
    @Value("${alphaVantage.apiKey}")
    private String alphaVantageApiKey;

    private final RestTemplate restTemplate;

    public MarketStatusService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> getTopGainers() {
        return getTopPerformers("top_gainers");
    }

    public ResponseEntity<String> getTopLosers() {
        return getTopPerformers("top_losers");
    }

    private ResponseEntity<String> getTopPerformers(String performersType) {
        String apiUrl = "https://www.alphavantage.co/query?function=TOP_GAINERS_LOSERS&apikey=" + alphaVantageApiKey;

        try {
            String response = restTemplate.getForObject(apiUrl, String.class);

            // Process the JSON response to extract the top performers and limit the response to 10 entries
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode performers = rootNode.get(performersType);

            ArrayNode simplifiedOutput = JsonNodeFactory.instance.arrayNode();
            int count=1;
            for (JsonNode performer : performers) {
                ObjectNode simplifiedMatch = JsonNodeFactory.instance.objectNode();
                simplifiedMatch.put("ticker", performer.get("ticker").asText());
                simplifiedMatch.put("price", performer.get("price").asText());
                simplifiedMatch.put("volume", performer.get("volume").asText());
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
            log.error("An error occurred while fetching " + performersType);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching " + performersType);
        }
    }
}

//@Service
//public class MarketStatusService {
//    @Value("${alphaVantage.apiKey}")
//    private String alphaVantageApiKey;
//
//    private final RestTemplate restTemplate;
//
//    public MarketStatusService(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }
//
////    public String getMarketStatus() {
////        String apiUrl = "https://www.alphavantage.co/query?function=TOP_GAINERS_LOSERS&apikey=" + alphaVantageApiKey;
////
////        String response = restTemplate.getForObject(apiUrl, String.class);
////        return response;
////    }
//}
