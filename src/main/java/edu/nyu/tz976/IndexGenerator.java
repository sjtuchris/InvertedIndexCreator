package edu.nyu.tz976;

import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class IndexGenerator {
    private BufferedOutputStream invertedIndexOutStream;
    private BufferedReader sortedPostingsInStream;
    private HashMap<String, LexiconWordTuple> lexiconMap = new HashMap<>();
    private long startByte = 0;
    private long endByte = -1;

    public void processTempPostings() {
        Path invertedIndexPath = Paths.get("./output/invertedIndex.txt");
        Path sortedPostingsPath = Paths.get("./output/sortedPostings.txt");

        File invertedFile = invertedIndexPath.toFile();
        File postingsFile = sortedPostingsPath.toFile();

        String wordFlag = null;
        List<Entry<String, String>> docFreqPairList = new ArrayList<>();


        try {
            invertedFile.createNewFile();
            sortedPostingsInStream = new BufferedReader(new FileReader(postingsFile));
            invertedIndexOutStream = new BufferedOutputStream(new FileOutputStream(invertedFile, true));

            String line;
            while ((line = sortedPostingsInStream.readLine()) != null) {

                // For tempPostings, each single line is: "word DOC_ID frequency"
                String[] postingSplit = line.split("\t");
                if (postingSplit.length == 3) {
                    if (wordFlag == null) {
                        wordFlag = postingSplit[0];
                    }

                    //postingSplit[1] is DOC_ID, postingSplit[2] is frequency
                    Entry<String, String> docFreqPair = new SimpleEntry<String, String>(postingSplit[1], postingSplit[2]);

                    // Before comes to the next word, write current word's inverted index list and update its lexicon
                    if (!wordFlag.equals(postingSplit[0])) {
                        writeInvertedIndex(docFreqPairList);

                        LexiconWordTuple lexiconTuple = new LexiconWordTuple(String.valueOf(startByte), String.valueOf(endByte), String.valueOf(docFreqPairList.size()));
                        lexiconMap.put(wordFlag, lexiconTuple);

                        docFreqPairList = new ArrayList<>();
                        wordFlag = postingSplit[0];
                    }
                    docFreqPairList.add(docFreqPair);

                }
            }

            sortedPostingsInStream.close();
            invertedIndexOutStream.close();
            OutputUtils.writeLexiconMap(lexiconMap);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeInvertedIndex(List<Entry<String, String>> docFreqParList) {

        // Count the byte location while writing to inverted index list
        startByte = endByte + 1;

        for (Entry<String, String> docFreqPair:docFreqParList) {
            try {
                byte[] byteData = compressDocFreqPair(docFreqPair.getKey(), docFreqPair.getValue());

                invertedIndexOutStream.write(byteData);
                endByte = endByte + byteData.length;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    // Store (DOC_ID, frequency) pair into an Integer list and compress
    private byte[] compressDocFreqPair(String docId, String frequency) {
        List<Integer> docFreqPair = new ArrayList<>();
        docFreqPair.add(Integer.valueOf(docId));
        docFreqPair.add(Integer.valueOf(frequency));

        byte[] compressed = VarByteCompressor.encode(docFreqPair);

        return compressed;
    }
}
