package edu.nyu.webSearchEngine.QueryExecutor;

import edu.nyu.webSearchEngine.Compressor;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

public class DAATUtils {
    private final static String invertedIndexFile = "./output/invertedIndex.txt";

    public static InvertedIndexMeta loadInvertedIndexMeta(long startByte) {
        InvertedIndexMeta meta = new InvertedIndexMeta();
        try {
            RandomAccessFile fileStore = new RandomAccessFile(invertedIndexFile, "rw");
            byte[] record = new byte[4];

            fileStore.seek(startByte);
            fileStore.readFully(record, 0, 4);
            meta.lastDocIdListLength = Compressor.byteArrayToInt(record);

            fileStore.seek(startByte+4);
            fileStore.readFully(record, 0, 4);
            meta.sizeListLength = Compressor.byteArrayToInt(record);

            record = new byte[meta.lastDocIdListLength];
            fileStore.seek(startByte+8);
            fileStore.readFully(record, 0, meta.lastDocIdListLength);
            meta.lastDocIdList = Compressor.varByteDecode(record);

            record = new byte[meta.sizeListLength];
            fileStore.seek(startByte+8+meta.lastDocIdListLength);
            fileStore.readFully(record, 0, meta.sizeListLength);
            meta.docIdAndFreqSizeList = Compressor.varByteDecode(record);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return meta;
    }

    public static List<Integer> retrieveDataFromInvertedIndex(long startByte, int length, RandomAccessFile fileStore) {
        byte[] record = new byte[length];
        try {
            fileStore.seek(startByte);
            fileStore.readFully(record, 0, length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Compressor.varByteDecode(record);
    }
}
