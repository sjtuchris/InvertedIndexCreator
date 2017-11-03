package edu.nyu.tz976.BackendServer;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.nyu.tz976.QueryExecutor.QueryProcessOrchestrator;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryHandler implements HttpHandler {
    private QueryProcessOrchestrator queryProcessOrchestrator;

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        String input = httpExchange.getRequestURI().getQuery();
        List<QueryResponse> responseList = queryProcessOrchestrator.executeQuery(input);

        OutputStream os = httpExchange.getResponseBody();

        Gson gson = new Gson();

        String output = "";

        if (responseList == null) {
            httpExchange.sendResponseHeaders(200, 1000);
            os.write("No results found!".getBytes());
        } else {
            output = gson.toJson(responseList);
            httpExchange.sendResponseHeaders(200, output.getBytes().length);
            os.write(output.getBytes());
        }

        os.close();
    }

    public void setQueryProcessOrchestrator(QueryProcessOrchestrator orchestrator) {
        this.queryProcessOrchestrator = orchestrator;
    }

}
