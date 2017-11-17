package edu.nyu.webSearchEngine;

import edu.nyu.webSearchEngine.InvertedIndexGenerator.InvertedIndexOrchestrator;
import edu.nyu.webSearchEngine.QueryExecutor.QueryProcessOrchestrator;

import java.util.Scanner;

public class AdminFunction {
    public static void adminFunction() {
        Scanner reader = new Scanner(System.in);
        System.out.println("Select function to run: ");
        System.out.println("A:  InvertedIndexGenerator");
        System.out.println("B:  QueryProcessor");
        System.out.println("C:  InvertedListSeeker");
        System.out.println("D:  Test");
        System.out.println("E:  Prepare dataSet");
        System.out.println("\nAttention:  If you choose A, original index data will be removed!");
        System.out.print("Choose function: ");
        System.out.println();

        String input = reader.nextLine();
        switch (input) {
            case "A":
                InvertedIndexOrchestrator invertedIndexOrchestrator = new InvertedIndexOrchestrator();
                invertedIndexOrchestrator.createInvertedIndexList();
                break;

            case "B":
                QueryProcessOrchestrator queryProcessOrchestrator = new QueryProcessOrchestrator();
                queryProcessOrchestrator.preLoadMetaData();
                App.startServer(queryProcessOrchestrator);
                break;

            case "C":
                InvertedListSeeker seeker = new InvertedListSeeker();
                seeker.metaTest();
                break;

            case "D":
                Test createInvertedIndexList = new Test();
                createInvertedIndexList.testMongo();
                break;

            case "E":
                InvertedIndexOrchestrator invertedIndexOrchestrator1 = new InvertedIndexOrchestrator();
                invertedIndexOrchestrator1.prepareDataSet();
                break;

            default:
                System.out.println("Invalid selection!");
                break;
        }
    }
}
