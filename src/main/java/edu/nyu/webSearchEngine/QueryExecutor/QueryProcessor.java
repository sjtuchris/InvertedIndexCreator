package edu.nyu.webSearchEngine.QueryExecutor;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class QueryProcessor {
    public PriorityQueue<DocIdWithBmValue> docIdBmValueQueue;

    private int[] freqOffsetArray;
    private int[] lastDocIdIdxArray;
    private int[] docIdIdxArray;

    public void processANDQuery(List<InvertedIndexMeta> metaList, int totalDocNum, PageUrlTableLoader pageUrlTableLoader) {
        docIdBmValueQueue = new PriorityQueue<DocIdWithBmValue>(11, new BMValueComparatorReverse());

        // Open each inverted list
        metaList = openList(metaList);
        freqOffsetArray = new int[metaList.size()];
        lastDocIdIdxArray = new int[metaList.size()];
        docIdIdxArray = new int[metaList.size()];

        int did = 0;
        int maxId = getMaxId(metaList.get(0));

        while (did <= maxId) {
            // First list, get next docId
            did = nextGEQ(metaList.get(0), did, 0);
            int tempId = did;


            // Skip this part if we only have one keyword
            if (metaList.size() > 1) {
                for (int i=1; i<metaList.size(); i++) {
                    tempId = nextGEQ(metaList.get(i), did, i);
                    if (tempId != did) break;
                }
            }

            if (tempId > did) {
                did = tempId;
            } else {
                int[] freq = new int[metaList.size()];
                for (int i=0; i<metaList.size(); i++) {
                    freq[i] = getFreq(metaList.get(i), did, i);
                }
                double bmValue = BMCalculator.getBMValue(totalDocNum, metaList, freq,
                        Integer.valueOf(pageUrlTableLoader.pageUrlTable.get(String.valueOf(did)).get(1)),
                        pageUrlTableLoader.avgLength);

                // Maintain the queue size no more than 10
                docIdBmValueQueue.add(new DocIdWithBmValue(did, bmValue, freq));
                if (docIdBmValueQueue.size() > 10) {
                    docIdBmValueQueue.poll();
                }

                did++;
            }

        }
        closeList(metaList);
    }

    public void processORQuery(List<InvertedIndexMeta> metaList, int totalDocNum, PageUrlTableLoader pageUrlTableLoader) {
        docIdBmValueQueue = new PriorityQueue<DocIdWithBmValue>(11, new BMValueComparatorReverse());
        HashMap<Integer, DocIdWithBmValue> docBmValueMap = new HashMap<>();

        // Open each inverted list
        metaList = openList(metaList);
        freqOffsetArray = new int[metaList.size()];
        lastDocIdIdxArray = new int[metaList.size()];
        docIdIdxArray = new int[metaList.size()];

        for (int term = 0; term<metaList.size(); term++) {
            int did = 0;
            int maxId = getMaxId(metaList.get(term));

            while(did <= maxId) {
                did = nextGEQ(metaList.get(term), did, term);

                // If doc already visited, accumulate BMValue, otherwise create new entry
                if(docBmValueMap.containsKey(did)) {
                    DocIdWithBmValue oldObj = docBmValueMap.get(did);
                    double bmValue = oldObj.bmValue;
                    int[] freq = oldObj.freq;

                    freq[term] = getFreq(metaList.get(term), did, term);
                    bmValue += BMCalculator.getBMValue(totalDocNum, metaList, freq,
                            Integer.valueOf(pageUrlTableLoader.pageUrlTable.get(String.valueOf(did)).get(1)),
                            pageUrlTableLoader.avgLength);

                    docBmValueMap.put(did, new DocIdWithBmValue(did, bmValue, freq));
                } else {
                    int[] freq = new int[metaList.size()];
                    freq[term] = getFreq(metaList.get(term), did, term);
                    double bmValue = BMCalculator.getBMValue(totalDocNum, metaList, freq,
                            Integer.valueOf(pageUrlTableLoader.pageUrlTable.get(String.valueOf(did)).get(1)),
                            pageUrlTableLoader.avgLength);
                    docBmValueMap.put(did, new DocIdWithBmValue(did, bmValue, freq));
                }

                did++;
            }
        }
        closeList(metaList);

        for(DocIdWithBmValue obj:docBmValueMap.values()) {
            docIdBmValueQueue.add(obj);
            if (docIdBmValueQueue.size() > 10) {
                docIdBmValueQueue.poll();
            }
        }
    }

    private List<InvertedIndexMeta> openList(List<InvertedIndexMeta> list) {
        String fileName = "./output/invertedIndex.txt";
        List<InvertedIndexMeta> metaList = new ArrayList<>();
        for (InvertedIndexMeta meta:list) {
            try {
                meta.accessFile = new RandomAccessFile(fileName, "rw");
                metaList.add(meta);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return metaList;
    }

    private void closeList(List<InvertedIndexMeta> metaList) {
        for (InvertedIndexMeta meta:metaList) {
            try {
                meta.accessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int nextGEQ(InvertedIndexMeta meta, int did, int termId) {
        if (did > meta.lastDocIdList.get(meta.lastDocIdList.size()-1)) return -1;

        int lastDocIdIdx;
        for (lastDocIdIdx = 0; lastDocIdIdx<meta.lastDocIdList.size(); lastDocIdIdx++) {
            if (meta.lastDocIdList.get(lastDocIdIdx) == did) {
                return did;
            } else if (meta.lastDocIdList.get(lastDocIdIdx) > did) {
                break;
            }
        }

        int offSet = 0;
        for (int i=0; i<lastDocIdIdx*2; i++) {
            offSet += meta.docIdAndFreqSizeList.get(i);
        }

        List<Integer> chunkDocIdList = DAATUtils.retrieveDataFromInvertedIndex(
                Long.valueOf(meta.lexiconWordTuple.startByte)+8+meta.lastDocIdListLength+meta.sizeListLength+offSet,
                meta.docIdAndFreqSizeList.get(lastDocIdIdx*2),
                meta.accessFile);

        offSet += meta.docIdAndFreqSizeList.get(lastDocIdIdx*2);
        freqOffsetArray[termId] = offSet;
        lastDocIdIdxArray[termId] = lastDocIdIdx;


        for (int i=0; i<chunkDocIdList.size(); i++) {
            int docId = chunkDocIdList.get(i);
            if (docId >= did) {
                docIdIdxArray[termId] = i;
                return docId;
            }
        }
        return -1;
    }

    private int getFreq(InvertedIndexMeta meta, int did, int termId) {
        int offSet = freqOffsetArray[termId];
        int lastDocIdIdx = lastDocIdIdxArray[termId];
        int idx = docIdIdxArray[termId];

        List<Integer> chunkFreqList = DAATUtils.retrieveDataFromInvertedIndex(
                Long.valueOf(meta.lexiconWordTuple.startByte)+8+meta.lastDocIdListLength+meta.sizeListLength+offSet,
                meta.docIdAndFreqSizeList.get(lastDocIdIdx*2+1),
                meta.accessFile);

        return chunkFreqList.get(idx);
    }

    private int getMaxId(InvertedIndexMeta meta) {
        List<Integer> list = meta.lastDocIdList;
        return list.get(list.size()-1);
    }

}
