package edu.nyu.tz976;

import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IndexGenerator {
    private BufferedOutputStream invertedIndexOutStream;
    private BufferedReader sortedPostingsInStream;
    private HashMap<String, LexiconWordTuple> lexiconMap = new HashMap<>();

    // Those two are for the location of a block's meta data
    private long startByte = 0;
    private long endByte = -1;

    public void processTempPostings() {
        Path invertedIndexPath = Paths.get("./output/invertedIndex.txt");
        Path sortedPostingsPath = Paths.get("./output/sortedPostings.txt");

        File invertedFile = invertedIndexPath.toFile();
        File postingsFile = sortedPostingsPath.toFile();

        String wordFlag = null;
        List<String> docIdList = new ArrayList<>();
        List<String> freqList = new ArrayList<>();

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
                    String docId = postingSplit[1];
                    String freq = postingSplit[2];

                    // Before comes to the next word, write current word's inverted index list and update its lexicon
                    if (!wordFlag.equals(postingSplit[0])) {
                        blockWiseCompressAndWriteInvertedIndexList(docIdList, freqList);
                        LexiconWordTuple lexiconTuple = new LexiconWordTuple(String.valueOf(startByte), String.valueOf(endByte), String.valueOf(docIdList.size()));
                        lexiconMap.put(wordFlag, lexiconTuple);

                        docIdList = new ArrayList<>();
                        freqList = new ArrayList<>();
                        wordFlag = postingSplit[0];
                    }
                    docIdList.add(docId);
                    freqList.add(freq);
                }
            }

            sortedPostingsInStream.close();
            invertedIndexOutStream.close();
            OutputUtils.writeLexiconMap(lexiconMap);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void blockWiseCompressAndWriteInvertedIndexList(List<String> docIdList, List<String> freqList) {

        // We form a chunk for every 128 (doc,freq) pair
        int chunkCounter = 0;
        // For each chunk
        List<Integer> chunkDocIdList = new ArrayList<>();
        List<Integer> chunkFreqList = new ArrayList<>();
        // For the entire block
        byte[] blockData = {};
        List<Integer> lastChunkDocIdList = new ArrayList<>();
        List<Integer> chunkSizeList = new ArrayList<>();

        for (int idx = 0; idx < docIdList.size(); idx++) {
            chunkDocIdList.add(Integer.valueOf(docIdList.get(idx)));
            chunkFreqList.add(Integer.valueOf(freqList.get(idx)));
            chunkCounter++;

            if (chunkCounter >= 128 || idx == (docIdList.size() - 1)) {
                byte[] chunkDocIdListData = Compressor.varByteEncode(chunkDocIdList);
                byte[] chunkFreqListData = Compressor.varByteEncode(chunkFreqList);
                Integer lastDocId = chunkDocIdList.get(chunkDocIdList.size()-1);

                // Block data part
                blockData = ArrayUtils.addAll(blockData, chunkDocIdListData);
                blockData = ArrayUtils.addAll(blockData, chunkFreqListData);

                // Meta part
                lastChunkDocIdList.add(lastDocId);
                // In chunkSizeList, for each chunk, it occupies two elements, one for docList, one for freqList
                chunkSizeList.add(chunkDocIdListData.length);
                chunkSizeList.add(chunkFreqListData.length);

                // Reset for next chunk
                chunkDocIdList = new ArrayList<>();
                chunkFreqList = new ArrayList<>();
                chunkCounter = 0;
            }
        }

        writeInvertedIndex(blockData, lastChunkDocIdList, chunkSizeList);

//        for (Entry<String, String> docFreqPair:docFreqParList) {
//            try {
//                byte[] byteData = compressDocFreqPair(docFreqPair.getKey(), docFreqPair.getValue());
//
//                invertedIndexOutStream.write(byteData);
//                endByte = endByte + byteData.length;
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

    }

    // The meta structure is: metaDocListLength, metaChunkSizeLength, lastChunkDocIdList, chunkSizeList, chunks...
    private void writeInvertedIndex(byte[] blockData, List<Integer> lastChunkDocIdList, List<Integer> chunkSizeList) {
        // Count the byte location while writing to inverted index list
        startByte = endByte + 1;

        System.out.println(lastChunkDocIdList.size());

        byte[] lastChunkDocId = Compressor.varByteEncode(lastChunkDocIdList);
        byte[] chunkSize = Compressor.varByteEncode(chunkSizeList);

        int metaDocListLengthInt = lastChunkDocId.length;
        int metaChunkSizeLengthInt = chunkSize.length;
        byte[] metaDocListLengthByte = Compressor.intToByteArray(metaDocListLengthInt);
        byte[] metaChunkSizeLengthByte = Compressor.intToByteArray(metaChunkSizeLengthInt);

        byte[] data = ArrayUtils.addAll(metaDocListLengthByte, metaChunkSizeLengthByte);
        data = ArrayUtils.addAll(data, lastChunkDocId);
        data = ArrayUtils.addAll(data, chunkSize);
        data = ArrayUtils.addAll(data, blockData);

        try {
            invertedIndexOutStream.write(data);
            endByte = endByte + data.length;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Store (DOC_ID, frequency) pair into an Integer list and compress
    private byte[] compressDocFreqPair(String docId, String frequency) {
        List<Integer> docFreqPair = new ArrayList<>();
        docFreqPair.add(Integer.valueOf(docId));
        docFreqPair.add(Integer.valueOf(frequency));

        return Compressor.varByteEncode(docFreqPair);
    }


}
