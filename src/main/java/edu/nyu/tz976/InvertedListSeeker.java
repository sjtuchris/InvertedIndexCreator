package edu.nyu.tz976;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Scanner;

public class InvertedListSeeker {
    public void metaTest() {
        Scanner reader = new Scanner(System.in);
        System.out.println("Input the start byte: ");
        long start = reader.nextLong();
        reader.close();
        System.out.println("");

        String fileName = "./output/invertedIndex.txt";
        readFromRandomAccessFile(fileName, start, 0L);
    }

    public void seekWord() {
        Scanner reader = new Scanner(System.in);
        System.out.println("Input the start byte: ");
        long start = reader.nextLong();
        System.out.println("Input the end byte: ");
        long end = reader.nextLong();
        reader.close();
        System.out.println("");

        String fileName = "./output/invertedIndex.txt";
        readFromRandomAccessFile(fileName, start, end);
    }

    public void readFromRandomAccessFile(String file, long start, long end) {
//        byte[] record = new byte[(int)(end-start+1)];
        byte[] record = new byte[4];
        try {
            RandomAccessFile fileStore = new RandomAccessFile(file, "rw");
            // moves file pointer to position specified
             fileStore.seek(start);
            // reading String from RandomAccessFile
             fileStore.readFully(record, 0, 4);

            // No.1 4 bytes
            int docIdListLength = Compressor.byteArrayToInt(record);
//            System.out.println(docIdListLength);

            fileStore.seek(start+4);
            fileStore.readFully(record, 0, 4);

            // No.2 4 bytes
            int sizeListLength = Compressor.byteArrayToInt(record);
//            System.out.println(sizeListLength);

            record = new byte[docIdListLength];
            fileStore.seek(start+8);
            fileStore.readFully(record, 0, docIdListLength);

            // No.3 last docId list
            showDecode(record);

            record = new byte[sizeListLength];
            fileStore.seek(start+8+docIdListLength);
            fileStore.readFully(record, 0, sizeListLength);

            // No.4 size list
            List<Integer> sizeList = Compressor.varByteDecode(record);
            showDecode(record);

            record = new byte[sizeList.get(0)];
            fileStore.seek(start+8+docIdListLength+sizeListLength);
            fileStore.readFully(record, 0, sizeList.get(0));
            List<Integer> list = Compressor.varByteDecode(record);
            showDecode(record);
            System.out.println(list.get(list.size()-1));
//
//            record = new byte[sizeList.get(1)];
//            fileStore.seek(start+8+docIdListLength+sizeListLength+sizeList.get(0));
//            fileStore.readFully(record, 0, sizeList.get(1));
//            showDecode(record);

            fileStore.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showDecode(byte[] line) {
        List<Integer> list = Compressor.varByteDecode(line);
        for (Integer i:list) {
            System.out.print(i);
            System.out.print(" ");
        }
        System.out.println();
    }
}
