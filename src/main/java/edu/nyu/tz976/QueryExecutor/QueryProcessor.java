package edu.nyu.tz976.QueryExecutor;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class QueryProcessor {
    public void processQuery(PriorityQueue<InvertedIndexMeta> metaQueue) {
        List<InvertedIndexMeta> metaList = new ArrayList<>();

        // Open each inverted list
        metaList = openList(metaQueue);

        int did = 0;
        int maxId = getMaxId(metaList.get(0));

        while (did <= maxId) {
            did = nextGEQ(metaList.get(0), did);
        }
    }

    private List<InvertedIndexMeta> openList(PriorityQueue<InvertedIndexMeta> metaQueue) {
        String fileName = "./output/invertedIndex.txt";
        List<InvertedIndexMeta> metaList = new ArrayList<>();
        while (!metaQueue.isEmpty()) {
            try {
                InvertedIndexMeta meta = metaQueue.poll();
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

    private int nextGEQ(InvertedIndexMeta meta, int did) {
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
                Long.getLong(meta.lexiconWordTuple.startByte)+8+meta.lastDocIdListLength+meta.sizeListLength+offSet,
                meta.docIdAndFreqSizeList.get(lastDocIdIdx*2),
                meta.accessFile);

        for (int docId:chunkDocIdList) {
            if (docId >= did) {
                return docId;
            }
        }

        return -1;
    }


    private int getMaxId(InvertedIndexMeta meta) {
        List<Integer> list = meta.lastDocIdList;
        return list.get(list.size()-1);
    }
}
