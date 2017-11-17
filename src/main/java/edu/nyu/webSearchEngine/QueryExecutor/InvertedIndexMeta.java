package edu.nyu.webSearchEngine.QueryExecutor;

import edu.nyu.webSearchEngine.LexiconWordTuple;

import java.io.RandomAccessFile;
import java.util.List;

public class InvertedIndexMeta {
    int lastDocIdListLength;
    int sizeListLength;
    String word;
    String numOfDocContainTerm;
    List<Integer> lastDocIdList;
    List<Integer> docIdAndFreqSizeList;
    LexiconWordTuple lexiconWordTuple;
    RandomAccessFile accessFile;
}
