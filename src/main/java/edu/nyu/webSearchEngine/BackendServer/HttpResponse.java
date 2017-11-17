package edu.nyu.webSearchEngine.BackendServer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class HttpResponse {
    public String header;
    public List<String> jsonResponseList = new ArrayList<>();

    public void addJsonResponse(String jsonResponse) {
        this.jsonResponseList.add(jsonResponse);
    }

    public static String toJson(HttpResponse httpResponse) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(httpResponse);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
