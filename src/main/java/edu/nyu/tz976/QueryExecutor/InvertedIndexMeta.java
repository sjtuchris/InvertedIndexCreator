package edu.nyu.tz976.QueryExecutor;

import edu.nyu.tz976.LexiconWordTuple;

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
