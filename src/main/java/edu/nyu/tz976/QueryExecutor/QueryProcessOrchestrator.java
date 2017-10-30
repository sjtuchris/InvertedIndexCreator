package edu.nyu.tz976.QueryExecutor;

import edu.nyu.tz976.LexiconWordTuple;

import java.util.*;

public class QueryProcessOrchestrator {
    public HashMap<String, LexiconWordTuple> lexicon = new HashMap<>();

    public void executeQuery() {
        LexiconLoader lexiconLoader = new LexiconLoader();
        lexiconLoader.loadLexicon();
        lexicon = lexiconLoader.lexiconMap;

        List<String> inputWords = handleInput();

        for (int i=0; i<inputWords.size(); i++) {
            String word = inputWords.get(i);
            System.out.println(word);
            System.out.println(lexicon.get(word).startByte);
        }

    }

    private List<String> handleInput() {
        Scanner reader = new Scanner(System.in);
        System.out.print("Input key words: ");
        System.out.println();
        String[] result = reader.nextLine().split("\\s*(=>|,|\\s)\\s*");
        return Arrays.asList(result);
    }
}
