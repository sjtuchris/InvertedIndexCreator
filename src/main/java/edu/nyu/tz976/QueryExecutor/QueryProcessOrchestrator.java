package edu.nyu.tz976.QueryExecutor;

import edu.nyu.tz976.LexiconWordTuple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class QueryProcessOrchestrator {
    public HashMap<String, LexiconWordTuple> lexicon = new HashMap<>();
    public String semanticsFlag;

    private static final Logger LOGGER = LogManager.getLogger(QueryProcessOrchestrator.class);

    public void executeQuery() {
        // Load lexicon
        LOGGER.info("Loading lexicon...");
        LexiconLoader lexiconLoader = new LexiconLoader();
        lexiconLoader.loadLexicon();
        lexicon = lexiconLoader.lexiconMap;
        int totalDocNum = Integer.valueOf(lexiconLoader.totalDocNum);
        LOGGER.info("Lexicon loaded!");

        LOGGER.info("Loading pageUrlTable...");
        PageUrlTableLoader pageUrlTableLoader = new PageUrlTableLoader();
        pageUrlTableLoader.loadPageUrlTable();
        LOGGER.info("PageUrlTable loaded!");

        // Input keyword for search
        List<String> inputWords = handleInput();

        // Load meta for those keywords. Priority: keyword with shortest inverted index list first
        LOGGER.info("Loading metadata...");
        PriorityQueue<InvertedIndexMeta> metaQueue = constructMetaQueue(inputWords);
        List<InvertedIndexMeta> metaList = queueToList(metaQueue);
        metaList = assignNumberOfDocContainTerm(metaList);

        // Execute query
        LOGGER.info("Executing query...");
        QueryProcessor queryProcessor = new QueryProcessor();
        if (semanticsFlag.equals("AND")) {
            queryProcessor.processANDQuery(metaList, totalDocNum, pageUrlTableLoader);
        } else {
            queryProcessor.processORQuery(metaList, totalDocNum, pageUrlTableLoader);
        }

        // Reverse the order based on BM25 value
        PriorityQueue<DocIdWithBmValue> outQueue = new PriorityQueue<>(11, new BMValueComparator());
        while (!queryProcessor.docIdBmValueQueue.isEmpty()) {
            DocIdWithBmValue pair = queryProcessor.docIdBmValueQueue.poll();
            outQueue.add(pair);
        }
        while (!outQueue.isEmpty()) {
            DocIdWithBmValue pair = outQueue.poll();
            System.out.println(String.valueOf(pair.docId)+" "+String.valueOf(pair.bmValue)+" "+Arrays.toString(pair.freq));
        }
    }

    private List<InvertedIndexMeta> assignNumberOfDocContainTerm(List<InvertedIndexMeta> list) {
        List<InvertedIndexMeta> metaList = new ArrayList<>();
        for (InvertedIndexMeta meta:list) {
            meta.numOfDocContainTerm = lexicon.get(meta.word).numOfDoc;
            metaList.add(meta);
        }
        return metaList;
    }

    private List<String> handleInput() {
        Scanner reader = new Scanner(System.in);
        System.out.print("Input key words: ");
        System.out.println();

        String input = reader.nextLine();
        //If contain "|", its semantics is OR
        if (input.contains("|")) {
            semanticsFlag = "OR";
            String[] result = input.split("\\|");
            return Arrays.asList(result);
        } else {
            semanticsFlag = "AND";
            String[] result = input.split("\\s*(=>|,|\\s)\\s*");
            return Arrays.asList(result);
        }
    }

    private PriorityQueue<InvertedIndexMeta> constructMetaQueue(List<String> inputWords) {
        Comparator<InvertedIndexMeta> metaComparator = new ChunkNumComparator();
        PriorityQueue<InvertedIndexMeta> metaQueue =
                new PriorityQueue<InvertedIndexMeta>(inputWords.size(), metaComparator);

        for (int i=0; i<inputWords.size(); i++) {
            String word = inputWords.get(i);
            LexiconWordTuple tuple = lexicon.get(word);
            InvertedIndexMeta meta = DAATUtils.loadInvertedIndexMeta(Long.valueOf(tuple.startByte));
            meta.lexiconWordTuple = tuple;
            meta.word = word;
            metaQueue.add(meta);
        }

        return metaQueue;
    }

    private List<InvertedIndexMeta> queueToList(PriorityQueue<InvertedIndexMeta> queue) {
        List<InvertedIndexMeta> metaList = new ArrayList<>();
        while (!queue.isEmpty()) {
            InvertedIndexMeta meta = queue.poll();
            metaList.add(meta);
        }
        return metaList;
    }

    // Shorted list first
    private class ChunkNumComparator implements Comparator<InvertedIndexMeta>
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
