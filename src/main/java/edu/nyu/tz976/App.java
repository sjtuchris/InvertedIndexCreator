package edu.nyu.tz976;

import com.sun.net.httpserver.HttpServer;
import edu.nyu.tz976.BackendServer.QueryHandler;
import edu.nyu.tz976.InvertedIndexGenerator.InvertedIndexOrchestrator;
import edu.nyu.tz976.QueryExecutor.QueryProcessOrchestrator;

import java.io.IOException;
import java.net.InetSocketAddress;

public class App
{
    public static void main( String[] args )
    {
        System.out.println( "Web Search Engine starts!" );

        // Choose the function you would like to run
        AdminFunction.adminFunction();
    }


    public static void startServer(QueryProcessOrchestrator queryProcessOrchestrator) {
        try{
            HttpServer server = HttpServer.create(new InetSocketAddress(8888), 0);

            QueryHandler handler = new QueryHandler();
            handler.setQueryProcessOrchestrator(queryProcessOrchestrator);

            server.createContext("/query", handler);
            server.setExecutor(null);
            server.start();
            System.out.println("The server is running");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
