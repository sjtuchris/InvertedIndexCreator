package edu.nyu.tz976.QueryExecutor;

import edu.nyu.tz976.LexiconWordTuple;

import java.util.*;

public class QueryProcessOrchestrator {
    public HashMap<String, LexiconWordTuple> lexicon = new HashMap<>();

    public void executeQuery() {
        // Load lexicon
        LexiconLoader lexiconLoader = new LexiconLoader();
        lexiconLoader.loadLexicon();
        lexicon = lexiconLoader.lexiconMap;
        int maxId = Integer.valueOf(lexiconLoader.totalDocNum);

        // Input keyword for search
        List<String> inputWords = handleInput();

        // Load meta for those keywords. Priority: keyword with shortest inverted index list first
        PriorityQueue<InvertedIndexMeta> metaQueue = constructMetaQueue(inputWords);

    }

    private List<String> handleInput() {
        Scanner reader = new Scanner(System.in);
        System.out.print("Input key words: ");
        System.out.println();
        String[] result = reader.nextLine().split("\\s*(=>|,|\\s)\\s*");
        return Arrays.asList(result);
    }

    private PriorityQueue<InvertedIndexMeta> constructMetaQueue(List<String> inputWords) {
        Comparator<InvertedIndexMeta> metaComparator = new chunkNumComparator();
        PriorityQueue<InvertedIndexMeta> metaQueue =
                new PriorityQueue<InvertedIndexMeta>(inputWords.size(), metaComparator);

        for (int i=0; i<inputWords.size(); i++) {
            String word = inputWords.get(i);
            LexiconWordTuple tuple = lexicon.get(word);
            InvertedIndexMeta meta = DAATUtils.loadInvertedIndexMeta(Long.getLong(tuple.startByte));
            meta.lexiconWordTuple = tuple;
            meta.word = word;
            metaQueue.add(meta);
        }

        return metaQueue;
    }

    // Shorted list first
    private class chunkNumComparator implements Comparator<InvertedIndexMeta>
    {
        @Override
        public int compare(InvertedIndexMeta x, InvertedIndexMeta y)
        {
            if (x.lastDocIdListLength < y.lastDocIdListLength)
            {
                return -1;
            }
            if (x.lastDocIdListLength > y.lastDocIdListLength)
            {
                return 1;
            }
            return 0;
        }
    }
}
