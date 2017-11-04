package edu.nyu.tz976.QueryExecutor;

import com.mongodb.client.MongoCollection;
import edu.nyu.tz976.LexiconWordTuple;
import edu.nyu.tz976.MongoDBUtils.MongoDBUtil;
import edu.nyu.tz976.BackendServer.QueryResponse;
import edu.nyu.tz976.SnippetGenerator.SnippetUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import java.util.*;

public class QueryProcessOrchestrator {
    // Load lexicon into a hash map
    public HashMap<String, LexiconWordTuple> lexicon = new HashMap<>();
    // Mark the query is an "AND" or "OR" query
    public String semanticsFlag;
    // Get total page number
    int totalDocNum;
    // Load pageUrlTable
    PageUrlTableLoader pageUrlTableLoader = new PageUrlTableLoader();

    private static final Logger LOGGER = LogManager.getLogger(QueryProcessOrchestrator.class);

    public void preLoadMetaData() {
        // Load lexicon
        LOGGER.info("Loading lexicon...");
        LexiconLoader lexiconLoader = new LexiconLoader();
        lexiconLoader.loadLexicon();
        lexicon = lexiconLoader.lexiconMap;
        totalDocNum = Integer.valueOf(lexiconLoader.totalDocNum);
        LOGGER.info("Lexicon loaded!");

        LOGGER.info("Loading pageUrlTable...");

        pageUrlTableLoader.loadPageUrlTable();
        LOGGER.info("PageUrlTable loaded!");
    }

    public List<QueryResponse> executeQuery(String input) {

        // Input keyword for search
        List<String> inputWords = inputHandler(input);

        // Load meta for those keywords. Priority: keyword with shortest inverted index list first
        LOGGER.info("Loading metadata...");
        PriorityQueue<InvertedIndexMeta> metaQueue = constructMetaQueue(inputWords);
        List<InvertedIndexMeta> metaList = queueToList(metaQueue);
        metaList = assignNumberOfDocContainTerm(metaList);

        // Execute query
        LOGGER.info("Executing query...");
        QueryProcessor queryProcessor = new QueryProcessor();

        // Response list
        List<QueryResponse> responseList = new ArrayList<>();

        // No doc that contains all the keywords for AND query, return false
        if (metaList.size() != inputWords.size() && semanticsFlag.equals("AND")){
            System.out.println("Cannot find any result, try OR query.");
            return null;
        } else {
            if (semanticsFlag.equals("AND")) {
                queryProcessor.processANDQuery(metaList, totalDocNum, pageUrlTableLoader);
            } else {
                queryProcessor.processORQuery(metaList, totalDocNum, pageUrlTableLoader);
            }

            // Reverse the order based on BM25 value
            PriorityQueue<DocIdWithBmValue> outQueue = reversePriorityQueue(queryProcessor.docIdBmValueQueue);



            while (!outQueue.isEmpty()) {
                DocIdWithBmValue pair = outQueue.poll();
                int docId = pair.docId;

                String url = pageUrlTableLoader.pageUrlTable.get(String.valueOf(docId)).get(0);

                // Get content data from mongoDB
                MongoCollection<Document> collection = MongoDBUtil.getMongoCollection();
                Document doc = MongoDBUtil.getRecord(collection, docId);
                String content = doc.values().toString();

                List<String> snippets = SnippetUtils.generateSnippets(content, inputWords);

                QueryResponse response = new QueryResponse(docId, pair.bmValue, freqSequence(pair.freq, inputWords), url, snippets);
                responseList.add(response);

            }
            return responseList;
        }
    }

    // Get number of pages that contain this word from lexicon
    private List<InvertedIndexMeta> assignNumberOfDocContainTerm(List<InvertedIndexMeta> list) {
        List<InvertedIndexMeta> metaList = new ArrayList<>();
        for (InvertedIndexMeta meta:list) {
            meta.numOfDocContainTerm = lexicon.get(meta.word).numOfDoc;
            metaList.add(meta);
        }
        return metaList;
    }

    private List<String> manualHandleInput() {
        Scanner reader = new Scanner(System.in);
        System.out.print("Input key words: ");
        System.out.println();

        String input = reader.nextLine();
        //If contain "@", its semantics is OR
        if (input.contains("@")) {
            semanticsFlag = "OR";
            String[] result = input.split("\\|");
            return Arrays.asList(result);
        } else {
            semanticsFlag = "AND";
            String[] result = input.split("\\s*(=>|,|\\s)\\s*");
            return Arrays.asList(result);
        }
    }

    private List<String> inputHandler(String input) {
        //If contain "@", its semantics is OR
        if (input.contains("@")) {
            semanticsFlag = "OR";
            String[] result = input.split("@");
            return Arrays.asList(result);
        } else {
            //If contain "&", its semantics is AND
            semanticsFlag = "AND";
            String[] result = input.split("&");
            return Arrays.asList(result);
        }
    }

    // Construct priority queue for metadata of each word
    private PriorityQueue<InvertedIndexMeta> constructMetaQueue(List<String> inputWords) {
        Comparator<InvertedIndexMeta> metaComparator = new ChunkNumComparator();
        PriorityQueue<InvertedIndexMeta> metaQueue =
                new PriorityQueue<InvertedIndexMeta>(inputWords.size(), metaComparator);

        for (int i=0; i<inputWords.size(); i++) {
            String word = inputWords.get(i);
            // Load start byte from lexicon
            LexiconWordTuple tuple = lexicon.get(word);

            if (tuple != null) {
                // Based on the start byte, retrieve meta data from inverted index
                InvertedIndexMeta meta = DAATUtils.loadInvertedIndexMeta(Long.valueOf(tuple.startByte));
                meta.lexiconWordTuple = tuple;
                meta.word = word;
                metaQueue.add(meta);
            }
        }

        return metaQueue;
    }

    // Internal tool
    private List<InvertedIndexMeta> queueToList(PriorityQueue<InvertedIndexMeta> queue) {
        List<InvertedIndexMeta> metaList = new ArrayList<>();
        while (!queue.isEmpty()) {
            InvertedIndexMeta meta = queue.poll();
            metaList.add(meta);
        }
        return metaList;
    }

    // Internal tool
    private PriorityQueue<DocIdWithBmValue> reversePriorityQueue(PriorityQueue<DocIdWithBmValue> queue) {
        PriorityQueue<DocIdWithBmValue> outQueue = new PriorityQueue<>(11, new BMValueComparator());
        while (!queue.isEmpty()) {
            DocIdWithBmValue pair = queue.poll();
            outQueue.add(pair);
        }
        return outQueue;
    }

    // Output freq data in a nicer format
    private String freqSequence(int[] freq, List<String> inputWords) {
        String sequence = "";

        for (int i = 0; i<freq.length; i++) {
            sequence = sequence + " " + inputWords.get(i) + " : " + String.valueOf(freq[i]) + ";";
        }

        return sequence;
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
