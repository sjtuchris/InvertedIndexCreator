package edu.nyu.webSearchEngine.BackendServer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.nyu.webSearchEngine.QueryExecutor.QueryProcessOrchestrator;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class QueryHandler implements HttpHandler {
    private QueryProcessOrchestrator queryProcessOrchestrator;

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        String input = httpExchange.getRequestURI().getQuery();
        List<QueryResponse> responseList = queryProcessOrchestrator.executeQuery(input);

        OutputStream os = httpExchange.getResponseBody();

        String output = "";

        if (responseList == null) {
            httpExchange.sendResponseHeaders(200, 1000);
            os.write("No results found!".getBytes());
        } else {
            // If you want to get Json string, use gson as following
            // Gson gson = new Gson();
            // output = gson.toJson(responseList);

            //Generate XML file
            XMLCreator xmlCreator = new XMLCreator();
            output = xmlCreator.getDocumentString(xmlCreator.createDocument(responseList));

            httpExchange.sendResponseHeaders(200, output.getBytes().length);
            os.write(output.getBytes());
        }

        os.close();
    }

    public void setQueryProcessOrchestrator(QueryProcessOrchestrator orchestrator) {
        this.queryProcessOrchestrator = orchestrator;
    }

}
