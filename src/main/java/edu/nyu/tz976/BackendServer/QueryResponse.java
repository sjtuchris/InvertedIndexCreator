package edu.nyu.tz976.BackendServer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class QueryResponse {
    public int docId;
    public double bmValue;
    public String freq;
    public String url;
    public List<String> snippets;

    public QueryResponse(int docId, double bmValue, String freq, String url, List<String> snippets) {
        this.docId = docId;
        this.bmValue = bmValue;
        this.freq = freq;
        this.url = url;
        this.snippets = snippets;
    }

    // Object to Json String
    public static String getJsonResponse(QueryResponse queryResponse) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(queryResponse);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
