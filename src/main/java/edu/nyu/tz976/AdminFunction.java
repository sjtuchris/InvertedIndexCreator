package edu.nyu.tz976;

import edu.nyu.tz976.InvertedIndexGenerator.InvertedIndexOrchestrator;
import edu.nyu.tz976.QueryExecutor.QueryProcessOrchestrator;

import java.util.Scanner;

public class AdminFunction {
    public static void adminFunction() {
        Scanner reader = new Scanner(System.in);
        System.out.println("Select function to run: ");
        System.out.println("A:  InvertedIndexGenerator");
        System.out.println("B:  InvertedListSeeker");
        System.out.println("C:  QueryProcessor");
        System.out.println("D:  Test");
        System.out.print("Choose function: ");
        System.out.println();

        String input = reader.nextLine();
        switch (input) {
            case "A":
                InvertedIndexOrchestrator invertedIndexOrchestrator = new InvertedIndexOrchestrator();
                invertedIndexOrchestrator.createInvertedIndexList();
                break;
            case "B":
                InvertedListSeeker seeker = new InvertedListSeeker();
                seeker.metaTest();
                break;
            case "C":
                QueryProcessOrchestrator queryProcessOrchestrator = new QueryProcessOrchestrator();
                queryProcessOrchestrator.executeQuery("");
                break;
            case "D":
                Test createInvertedIndexList = new Test();
                createInvertedIndexList.testMongo();
                break;

            default:
                System.out.println("Invalid selection!");
                break;
        }
    }
}